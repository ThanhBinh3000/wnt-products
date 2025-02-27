package vn.com.gsoft.products.service.impl;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.com.gsoft.products.constant.*;
import vn.com.gsoft.products.entity.Process;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.*;
import vn.com.gsoft.products.model.system.PaggingReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.model.system.WrapData;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.repository.feign.InventoryFeign;
import vn.com.gsoft.products.service.KafkaProducer;
import vn.com.gsoft.products.service.ThuocsService;
import vn.com.gsoft.products.util.system.DataUtils;
import vn.com.gsoft.products.util.system.ExportExcel;
import vn.com.gsoft.products.util.system.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
public class ThuocsServiceImpl extends BaseServiceImpl<Thuocs, ThuocsReq, Long> implements ThuocsService {

    private ThuocsRepository hdrRepo;

    @Autowired
    private KafkaProducer kafkaProducer;
    @Value("${wnt.kafka.internal.consumer.topic.import-master}")
    private String topicName;
    @Autowired
    private PhieuXuatsRepository phieuXuatsRepository;
    @Autowired
    private PhieuNhapsRepository phieuNhapsRepository;

    @Autowired
    public ThuocsServiceImpl(ThuocsRepository hdrRepo) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
    }

    @Autowired
    public NhomThuocsRepository nhomThuocsRepository;

    @Autowired
    public DonViTinhsRepository donViTinhsRepository;

    @Autowired
    public WarehouseLocationRepository warehouseLocationRepository;

    @Autowired
    public PhongKhamsRepository phongKhamsRepository;

    @Autowired
    public InventoryRepository inventoryRepository;

    @Autowired
    public NhaThuocsRepository nhaThuocsRepository;

    @Autowired
    public FileServiceImpl fileService;

    @Autowired
    public InventoryFeign inventoryFeign;
    @Autowired
    public ReplaceGoodsAndBundleGoodsRepository replaceGoodsAndBundleGoodsRepository;

    @Autowired
    public PhieuNhapChiTietsRepository phieuNhapChiTietsRepository;

    @Autowired
    public PhieuXuatChiTietsRepository phieuXuatChiTietsRepository;

    @Autowired
    public ConfigTemplateRepository configTemplateRepository;

    @Autowired
    public NhaCungCapsRepository nhaCungCapsRepository;

    @Autowired
    private  ConnectivityDrugRepository connectivityDrugRepository;

    @Autowired
    private DraftListDrugRepository draftListDrugRepository;

    //region Public Methods
    @Override
    public Page<Thuocs> searchPage(ThuocsReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        req.setNhaThuocMaNhaThuocCha(userInfo.getNhaThuoc().getMaNhaThuocCha());
        if (req.getDataDelete() != null) {
            req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
        }
        Page<Thuocs> thuocs = hdrRepo.searchPage(req, pageable);
        thuocs.getContent().forEach(item -> {
            if (item.getNhomThuocMaNhomThuoc() != null) {
                Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
                byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
            }
            if (req.getTypeService() != null && req.getTypeService() == 0) { //kiểm tra nếu là thuốc thì mới fill dữ liệu bên dưới
                if (item.getDonViThuNguyenMaDonViTinh() != null) {
                    Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
                    byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhThuNguyen(donViTinhs.getTenDonViTinh()));
                }
                if (item.getDonViXuatLeMaDonViTinh() != null) {
                    Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
                    byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhXuatLe(donViTinhs.getTenDonViTinh()));
                }
                if (item.getIdWarehouseLocation() != null) {
                    Optional<WarehouseLocation> byIdNt = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
                    byIdNt.ifPresent(warehouseLocations -> item.setTenViTri(warehouseLocations.getNameWarehouse()));
                }
            }
            Optional<ConnectivityDrug> connectivityDrug = connectivityDrugRepository.findByDrugIdAndDrugStoreIdAndRecordStatusId(item.getId(), item.getNhaThuocMaNhaThuoc(), RecordStatusContains.ACTIVE);
            if(connectivityDrug.isPresent()){
                item.setConnectivityDrugBaId(connectivityDrug.get().getId());
            }
        });
        return thuocs;
    }

    @Override
    public List<Thuocs> searchList(ThuocsReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
//		req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
//		req.setNhaThuocMaNhaThuocCha(userInfo.getNhaThuoc().getMaNhaThuocCha());
//		if(req.getDataDelete() != null){
//			req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
//		}
        List<Thuocs> thuocs = hdrRepo.searchList(req);
        thuocs.forEach(item -> {
            List<DonViTinhs> dviTinh = new ArrayList<>();
            if (item.getDonViXuatLeMaDonViTinh() > 0) {
                Optional<DonViTinhs> byId = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
                if (byId.isPresent()) {
                    byId.get().setFactor(1);
                    byId.get().setGiaBan(item.getGiaBanLe());
                    byId.get().setGiaNhap(item.getGiaNhap());
                    dviTinh.add(byId.get());
                    item.setTenDonViTinhXuatLe(byId.get().getTenDonViTinh());
                }
            }
            if (item.getDonViThuNguyenMaDonViTinh() > 0 && !item.getDonViThuNguyenMaDonViTinh().equals(item.getDonViXuatLeMaDonViTinh())) {
                Optional<DonViTinhs> byId = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
                if (byId.isPresent()) {
                    byId.get().setFactor(item.getHeSo());
                    byId.get().setGiaBan(item.getGiaBanLe().multiply(BigDecimal.valueOf(item.getHeSo())));
                    byId.get().setGiaNhap(item.getGiaNhap().multiply(BigDecimal.valueOf(item.getHeSo())));
                    dviTinh.add(byId.get());
                    item.setTenDonViTinhThuNguyen(byId.get().getTenDonViTinh());
                }
            }
            item.setListDonViTinhs(dviTinh);
            //fill vi tri tu/kho
            if (item.getIdWarehouseLocation() > 0) {
                Optional<WarehouseLocation> byId = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
                if (byId.isPresent()) {
                    item.setTenViTri(byId.get().getNameWarehouse());
                }
            }
            if (item.getNhomThuocMaNhomThuoc() != null) {
                Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
                byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
            }
            InventoryReq inventoryReq = new InventoryReq();
            inventoryReq.setDrugID(item.getId());
            inventoryReq.setDrugStoreID(item.getNhaThuocMaNhaThuoc());
            inventoryReq.setRecordStatusID(RecordStatusContains.ACTIVE);
            Optional<Inventory> inventory = inventoryRepository.searchDetail(inventoryReq);
            inventory.ifPresent(item::setInventory);
        });
        return thuocs;
    }

    @Override
    public Thuocs create(ThuocsReq req) throws Exception {
        return create(req, "thuốc");
    }

    @Override
    public Thuocs update(ThuocsReq req) throws Exception {
        return update(req, "thuốc");
    }

//    private void InsertReplaceAndBundleGood(int[] ids, int type, int drugId, string storeCode)
//    {
//        var repo = IoC.Resolve<BaseRepositoryV2<MedDbContext, ReplaceGoodsAndBundleGoods>>();
//        ids.ForEach(x =>
//                {
//                        var item = new ReplaceGoodsAndBundleGoods()
//                {
//                    DrugId = x,
//                    DrugIdMap = drugId,
//                    TypeId = type,
//                    DrugStoreCode = storeCode
//                };
//        repo.Insert(item);
//        repo.Commit();
//            });
//    }

    @Override
    public Thuocs create(ThuocsReq req, String object) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        String storeCode = userInfo.getNhaThuoc().getMaNhaThuoc();
        String parentStoreCode = userInfo.getNhaThuoc().getMaNhaThuocCha() != null && !userInfo.getNhaThuoc().getMaNhaThuocCha().isEmpty()
                ? userInfo.getNhaThuoc().getMaNhaThuocCha() : storeCode;
        String maThuoc = req.getMaThuoc();
        String tenThuoc = req.getTenThuoc();
        String barCode = req.getBarCode();
        //check trùng mã
        if (!maThuoc.isEmpty()) {
            Optional<Thuocs> byMaThuocAndRecordStatusId = hdrRepo.findByMaThuoc(maThuoc, storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
            if (byMaThuocAndRecordStatusId.isPresent()) {
                throw new Exception("Đã tồn tại mã " + object + ".");
            }
        }
        //check trùng tên
        if (!tenThuoc.isEmpty()) {
            Optional<Thuocs> byTenThuocAndRecordStatusId = hdrRepo.findByTenThuoc(tenThuoc, storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
            if (byTenThuocAndRecordStatusId.isPresent()) {
                throw new Exception("Đã tồn tại tên " + object + ".");
            }
        }
        //check trùng barcode
        if (!barCode.isEmpty()) {
            Optional<Thuocs> byBarcodeAndRecordStatusId = hdrRepo.findByBarCode(barCode, storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
            if (byBarcodeAndRecordStatusId.isPresent()) {
                throw new Exception("Đã tồn tại mã vạch " + object + ".");
            }
        }
        Thuocs hdr = new Thuocs();
        BeanUtils.copyProperties(req, hdr, "id");
        if (req.getRecordStatusId() == null) {
            hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        hdr.setStoreId(userInfo.getNhaThuoc().getId());
        hdr.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        hdr.setNhaThuocMaNhaThuocCreate(userInfo.getNhaThuoc().getMaNhaThuoc());
        hdr.setCreatedByUserId(userInfo.getId());
        hdr.setCreated(Date.from(Instant.now()));
        hdr.setMaThuoc(req.getMaThuoc().toUpperCase());
        hdr.setCodeHash((long) req.getMaThuoc().hashCode());
        hdr.setNameHash((long) req.getTenThuoc().hashCode());
        hdr.setDuTru(req.getGioiHan());
        hdr.setTypeService(TypeServiceContains.DRUG);
        hdr.setMetadataHash(hashCode());
        hdr.setRpMetadataHash(hdr.getRpHashCode());
        if (hdr.getDonViThuNguyenMaDonViTinh().equals(hdr.getDonViXuatLeMaDonViTinh())) {
            hdr.setDonViThuNguyenMaDonViTinh(null);
        }
        if (hdr.getGroupIdMapping() > 0) {
            hdr.setMappingDate(new Date());
            hdr.setStatusConfirm(StatusConfirmDrugContains.MAPPEDBYSYSTEM);
        } else {
            if (hdr.getFlag()) {
                hdr.setMappingDate(new Date());
                hdr.setStatusConfirm(StatusConfirmDrugContains.ADDNEW);
            }
        }

        hdr = hdrRepo.save(hdr);
        if (hdr.getId() > 0 && hdr.getFlag() && hdr.getGroupIdMapping() <= 0) {
            hdr.setGroupIdMapping(hdr.getId());
            hdr = hdrRepo.save(hdr);
        }
//        List<ReplaceGoodsAndBundleGoods> listReplaceGoods = replaceGoodsAndBundleGoodsRepository.findReplaceGoodsAndBundleGoodsByDrugStoreCodeAndAndDrugIdMap(storeCode, req.getId());
//        replaceGoodsAndBundleGoodsRepository.deleteAll(listReplaceGoods);
        saveReplaceGoodsAndBundleGoods(req, hdr.getId(), storeCode);
        return hdr;
    }

    @Override
    public Thuocs update(ThuocsReq req, String object) throws Exception {
        Profile userInfo = this.getLoggedUser();
        String storeCode = userInfo.getNhaThuoc().getMaNhaThuoc();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Optional<Thuocs> optional = hdrRepo.findById(req.getId());
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            String parentStoreCode = userInfo.getNhaThuoc().getMaNhaThuocCha() != null && !userInfo.getNhaThuoc().getMaNhaThuocCha().isEmpty()
                    ? userInfo.getNhaThuoc().getMaNhaThuocCha() : storeCode;
            String maThuoc = req.getMaThuoc();
            String tenThuoc = req.getTenThuoc();
            String barCode = req.getBarCode();
            //check đơn vị
            boolean canChangeUnits = canChangeUnits(parentStoreCode, req.getId(), req.getDonViXuatLeMaDonViTinh(), req.getDonViThuNguyenMaDonViTinh());
            if(!canChangeUnits) {
                if(req.getDonViXuatLeMaDonViTinh() > 0 && req.getDonViThuNguyenMaDonViTinh() == 0) {
                    boolean isHaveTran = checkUnitTran(req.getId());
                    if(isHaveTran) {
                        throw new Exception("Thuốc này đã tồn tại giao dịch nếu bạn cập nhật thì các giao dịch sẽ bị thay đổi.");
                    }
                    else {
                        throw new Exception("Thuốc này đã tồn tại 2 giao dịch không được cập nhật.");
                    }
                } else if (req.getDonViXuatLeMaDonViTinh() > 0 && req.getDonViThuNguyenMaDonViTinh() > 0) {
                    throw new Exception("Bạn không thể cập nhật với đơn vị tính này.");
                }
            }
            //check trùng mã
            if (!optional.get().getMaThuoc().equals(maThuoc) && !maThuoc.isEmpty()) {
                Optional<Thuocs> byMaThuocAndRecordStatusId = hdrRepo.findByMaThuoc(maThuoc, storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
                if (byMaThuocAndRecordStatusId.isPresent()) {
                    throw new Exception("Đã tồn tại mã " + object + ".");
                }
            }
            //check trùng tên
            if (!optional.get().getTenThuoc().equals(tenThuoc) && !tenThuoc.isEmpty()) {
                Optional<Thuocs> byTenThuocAndRecordStatusId = hdrRepo.findByTenThuoc(tenThuoc, storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
                if (byTenThuocAndRecordStatusId.isPresent()) {
                    throw new Exception("Đã tồn tại tên " + object + ".");
                }
            }
            //check trùng barcode
            if (!optional.get().getBarCode().equals(barCode) && !barCode.isEmpty()) {
                Optional<Thuocs> byBarcodeAndRecordStatusId = hdrRepo.findByBarCode(barCode, storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
                if (byBarcodeAndRecordStatusId.isPresent()) {
                    throw new Exception("Đã tồn tại mã vạch " + object + ".");
                }
            }
        }
        Thuocs hdr = optional.get();
        BeanUtils.copyProperties(req, hdr, "id");
        if (hdr.getRecordStatusId() == null) {
            hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        hdr.setStoreId(userInfo.getNhaThuoc().getId());
        hdr.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        hdr.setNhaThuocMaNhaThuocCreate(userInfo.getNhaThuoc().getMaNhaThuoc());
        hdr.setCreatedByUserId(userInfo.getId());
        hdr.setCreated(Date.from(Instant.now()));
        hdr.setMaThuoc(req.getMaThuoc().toUpperCase());
        hdr.setCodeHash((long) req.getMaThuoc().hashCode());
        hdr.setNameHash((long) req.getTenThuoc().hashCode());
        hdr.setDuTru(req.getGioiHan());
        hdr.setTypeService(TypeServiceContains.DRUG);
        hdr.setMetadataHash(hashCode());
        hdr.setRpMetadataHash(hdr.getRpHashCode());
        if (!req.getDonViXuatLeMaDonViTinh().equals(hdr.getDonViXuatLeMaDonViTinh())) {
            hdr.setSoDuDauKy(new BigDecimal(hdr.getHeSo()).multiply(hdr.getSoDuDauKy()));
        }
        if (hdr.getDonViXuatLeMaDonViTinh().equals(hdr.getDonViThuNguyenMaDonViTinh())) {
            hdr.setDonViThuNguyenMaDonViTinh(null);
        }
        hdr = hdrRepo.save(hdr);
        List<ReplaceGoodsAndBundleGoods> listReplaceGoods = replaceGoodsAndBundleGoodsRepository.findReplaceGoodsAndBundleGoodsByDrugStoreCodeAndDrugIdMap(storeCode, hdr.getId());
        replaceGoodsAndBundleGoodsRepository.deleteAll(listReplaceGoods);
        saveReplaceGoodsAndBundleGoods(req, hdr.getId(), storeCode);
        return hdr;
    }

    private void saveReplaceGoodsAndBundleGoods(ThuocsReq req, Long drugId, String storeCode){
        if(!req.getReplaceGoods().isEmpty()){
            for (ReplaceGoodsAndBundleGoodsReq replaceGood : req.getReplaceGoods()) {
                ReplaceGoodsAndBundleGoods replaceGoodsData = new ReplaceGoodsAndBundleGoods();
                BeanUtils.copyProperties(replaceGood, replaceGoodsData);
                replaceGoodsData.setDrugIdMap(drugId);
                replaceGoodsData.setTypeId(GoodsTypeMap.REPLACE_GOOD);
                replaceGoodsData.setDrugStoreCode(storeCode);
                replaceGoodsAndBundleGoodsRepository.save(replaceGoodsData);
            }
        }

        if(!req.getBundleGoods().isEmpty()){
            for (ReplaceGoodsAndBundleGoodsReq bundleGood : req.getBundleGoods()) {
                ReplaceGoodsAndBundleGoods bundleGoodsData = new ReplaceGoodsAndBundleGoods();
                BeanUtils.copyProperties(bundleGood, bundleGoodsData);
                bundleGoodsData.setDrugIdMap(drugId);
                bundleGoodsData.setTypeId(GoodsTypeMap.BUNDLE_GOOD);
                bundleGoodsData.setDrugStoreCode(storeCode);
                replaceGoodsAndBundleGoodsRepository.save(bundleGoodsData);
            }
        }
    }

    @Override
    public String generateDrugCode() throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        String drugCode = "TH1";
        var storeCode = userInfo.getNhaThuoc().getMaNhaThuoc();
        String parentStoreCode = userInfo.getNhaThuoc().getMaNhaThuocCha() != null && !userInfo.getNhaThuoc().getMaNhaThuocCha().isEmpty()
                ? userInfo.getNhaThuoc().getMaNhaThuocCha() : storeCode;
        ThuocsReq thuocsReq = new ThuocsReq();
        thuocsReq.setNhaThuocMaNhaThuoc(storeCode);
        thuocsReq.setNhaThuocMaNhaThuocCha(parentStoreCode);
        List<Thuocs> lst = hdrRepo.searchList(thuocsReq);
        Thuocs lastestThuoc = lst.stream()
                .sorted((d1, d2) -> d2.getId().compareTo(d1.getId()))
                .findFirst()
                .orElse(null);
        long count = lst.size();
        if (lastestThuoc != null) {
            int drugNum = 0;
            Matcher matcher = Pattern.compile("\\d+$").matcher(lastestThuoc.getMaThuoc());
            if (matcher.find()) {
                drugNum = Integer.parseInt(matcher.group());
            }
            drugNum++;
            if (drugNum < count - 2) {
                drugNum = (int) count - 2;
            }
            drugCode = "TH" + String.format("%04d", drugNum);
            String finalDrugCode = drugCode;
            while (lst.stream().anyMatch(d -> d.getMaThuoc().equals(finalDrugCode))) {
                drugNum++;
                drugCode = "TH" + String.format("%04d", drugNum);
            }
        }
        return drugCode;
    }

    @Override
    public String generateBarCode() throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        var storeCode = userInfo.getNhaThuoc().getMaNhaThuoc();
        String parentStoreCode = userInfo.getNhaThuoc().getMaNhaThuocCha() != null && !userInfo.getNhaThuoc().getMaNhaThuocCha().isEmpty()
                ? userInfo.getNhaThuoc().getMaNhaThuocCha() : storeCode;
        ThuocsReq thuocsReq = new ThuocsReq();
        thuocsReq.setNhaThuocMaNhaThuoc(storeCode);
        thuocsReq.setNhaThuocMaNhaThuocCha(parentStoreCode);
        List<Thuocs> lst = hdrRepo.searchList(thuocsReq);
        String temp = UUID.randomUUID().toString().replace("-", "");
        String barcode = temp.replaceAll("[a-zA-Z]", "").substring(0, 12);

        while (hdrRepo.findByBarCode(barcode, storeCode, parentStoreCode, RecordStatusContains.ACTIVE).isPresent()) {
            temp = UUID.randomUUID().toString().replace("-", "");
            barcode = temp.replaceAll("[a-zA-Z]", "").substring(0, 12);
        }

        // Check if storeCode contains only digits
        try {
            int number = Integer.parseInt(storeCode);
            int order = lst.isEmpty() ? 1 : lst.size() + 1;
            String storeCodeWithOrder = storeCode + order;

            if (barcode.length() > storeCodeWithOrder.length()) {
                barcode = barcode.substring(storeCodeWithOrder.length());
                // Replace the beginning of the barcode with storeCode + sequence number
                barcode = storeCodeWithOrder + barcode;
            }
        } catch (NumberFormatException e) {
            // storeCode contains non-numeric characters, ignore the numeric check
        }

        return barcode;
    }

    @Override
    public Thuocs uploadImage(FileDto req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Optional<Thuocs> optional = hdrRepo.findById(req.getDataId());
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }
        FileDto fileUpload = this.fileService.saveFile(req);
        if (fileUpload.getUrl() == null || fileUpload.getUrl().isEmpty()) {
            throw new Exception("Upload file không thành công");
        }
        Thuocs thuocs = optional.get();
        thuocs.setImagePreviewUrl(fileUpload.getUrl());
        thuocs.setImageThumbUrl(fileUpload.getUrl());
        hdrRepo.save(thuocs);
        return thuocs;
    }

    @Override
    public void export(ThuocsReq objReq, HttpServletResponse response) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        if (userInfo.getNhaThuoc().getMaNhaThuoc().equals(AppConstants.DictionaryStoreCode) || userInfo.getNhaThuoc().getMaNhaThuocCha().equals(AppConstants.DictionaryStoreCode))
            throw new Exception("Bạn không được phép xuất dữ liệu của cơ sở này.");
        PaggingReq paggingReq = new PaggingReq();
        paggingReq.setPage(0);
        paggingReq.setLimit(Integer.MAX_VALUE);
        objReq.setPaggingReq(paggingReq);
        Page<Thuocs> page = this.searchPage(objReq);
        List<Thuocs> data = page.getContent();


        String title = "Danh sách thuốc";
        String fileName = "danh-sach-thuoc.xlsx";
        String[] rowsName = new String[]{
                "Mã thuốc",
                "Nhóm thuốc",
                "Tên thuốc",
                "Thông tin",
                "Giá nhập",
                "Giá bán lẻ",
                "Trạng thái tích điểm\n" + "(0-bỏ tích,1-tích)",
                "Tích điểm(%)",
                "Trạng thái chiết khấu theo lợi nhuận\n" + "(0-bỏ tích, 1-tích)",
                "Chiết khấu hàng tư vấn cho nhân viên(%)",
                "Giá buôn",
                "Đơn vị lẻ",
                "Đơn vị thứ nguyên",
                "Số lượng cảnh báo",
                "Hệ số",
                "Số dư đầu kỳ",
                "Giá đầu kỳ",
                "Barcode",
                "Số Lô",
                "Hạn Dùng",
                "Dạng Bào Chế",
                "Số ĐK",
                "Hoạt Chất",
                "Hàm Lượng",
                "QC Đóng Gói", "Nước SX", "Hãng SX", "Nhà Nhập Khẩu", "ĐV Đóng Gói NN", "Giá Khai Báo", "Mã QG", "Loại Hàng", "CS Kê Khai", "Nước ĐK",
                "Địa Chỉ ĐK", "Địa Chỉ Sản Xuất", "Phân Loại", "Mã Định Danh", "Bán Buôn", "CTKM", "Điều kiện bảo quản", "Vị trí bảo quản", "Result"
        };
        List<Object[]> dataList = convertToExcelModel(data, rowsName, false);
        ExportExcel ex = new ExportExcel(title, fileName, rowsName, dataList, response);
        ex.export();
    }

    @Override
    public Page<Thuocs> colectionPageNotInPhieuKiemKe(ThuocsReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        req.setNhaThuocMaNhaThuocCha(userInfo.getNhaThuoc().getMaNhaThuocCha());
        if (req.getDataDelete() != null) {
            req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
        }
        Page<Thuocs> thuocs = hdrRepo.colectionPageNotInPhieuKiemKe(req, pageable);
        thuocs.getContent().forEach(item -> {
            if (item.getNhomThuocMaNhomThuoc() != null) {
                Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
                byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
            }
            if (item.getDonViThuNguyenMaDonViTinh() != null) {
                Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
                byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhThuNguyen(donViTinhs.getTenDonViTinh()));
            }
            if (item.getDonViXuatLeMaDonViTinh() != null) {
                Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
                byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhXuatLe(donViTinhs.getTenDonViTinh()));
            }
            if (item.getIdWarehouseLocation() != null) {
                Optional<WarehouseLocation> byIdNt = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
                byIdNt.ifPresent(warehouseLocations -> item.setTenViTri(warehouseLocations.getNameWarehouse()));
            }
            InventoryReq inventoryReq = new InventoryReq();
            inventoryReq.setDrugID(item.getId());
            inventoryReq.setDrugStoreID(item.getNhaThuocMaNhaThuoc());
            inventoryReq.setRecordStatusID(RecordStatusContains.ACTIVE);
            Optional<Inventory> inventory = inventoryRepository.searchDetail(inventoryReq);
            inventory.ifPresent(item::setInventory);
        });
        return thuocs;
    }

    @Override
    public Page<Thuocs> colectionPageHangDuTru(ThuocsReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        req.setNhaThuocMaNhaThuocCha(userInfo.getNhaThuoc().getMaNhaThuocCha());
        if (req.getDataDelete() != null) {
            req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
        }
        Page<Thuocs> thuocs = hdrRepo.colectionPagePhieuDuTru(req, pageable);
        thuocs.getContent().forEach(item -> {
            if (item.getNhomThuocMaNhomThuoc() != null) {
                Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
                byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
            }
            if (item.getDonViThuNguyenMaDonViTinh() != null) {
                Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
                byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhThuNguyen(donViTinhs.getTenDonViTinh()));
            }
            if (item.getDonViXuatLeMaDonViTinh() != null) {
                Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
                byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhXuatLe(donViTinhs.getTenDonViTinh()));
            }
            if (item.getIdWarehouseLocation() != null) {
                Optional<WarehouseLocation> byIdNt = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
                byIdNt.ifPresent(warehouseLocations -> item.setTenViTri(warehouseLocations.getNameWarehouse()));
            }
            InventoryReq inventoryReq = new InventoryReq();
            inventoryReq.setDrugID(item.getId());
            inventoryReq.setDrugStoreID(item.getNhaThuocMaNhaThuoc());
            inventoryReq.setRecordStatusID(RecordStatusContains.ACTIVE);
            HashMap<Integer, Double> inventory = getTotalInventory(inventoryReq);
            item.setLastValue(inventory.get(item.getId().intValue()));
        });
        return thuocs;
    }

    @Override
    public HashMap<Integer, Double> getTotalInventory(InventoryReq inventoryReq) {
        HashMap<Integer, Double> profile = inventoryFeign.getTotalInventory(inventoryReq);
        return profile;
    }


    // xem chi tiết số tồn ở các kho
    @Override
    public Object getDataDetailLastValueWarehouse(Long thuocId) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        var lstInventory = new Object();
        try {
            List<NhaThuocs> drugStores = new ArrayList<>();
            for (NhaThuocs ds : nhaThuocsRepository.findByMaNhaThuocChaAndHoatDong(userInfo.getNhaThuoc().getMaNhaThuocCha(), true)) {
                if (!ds.isConnectivity()) {
                    drugStores.add(ds);
                }
            }

            List<String> codeDrugStores = drugStores.stream()
                    .map(NhaThuocs::getMaNhaThuoc)
                    .collect(Collectors.toList());

            List<Object[]> items = inventoryRepository.findInventoryDetails(codeDrugStores, thuocId, RecordStatusContains.ACTIVE);

            lstInventory = items.stream()
                    .collect(Collectors.groupingBy(item -> (String) item[0]))
                    .entrySet().stream()
                    .map(e -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nameStore", e.getKey());
                        map.put("value", e.getValue().stream().map(item -> item[1]).findFirst().orElse(null));
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("GetDataDetailLastValueWarehouse {} error: {}", userInfo.getNhaThuoc().getMaNhaThuoc(), ex);
        }
        return  lstInventory;
    }

    @Override
    public Thuocs detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        String storeCode = userInfo.getNhaThuoc().getMaNhaThuoc();

        Optional<Thuocs> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        Thuocs thuocs = optional.get();
        //fill dvt
        List<DonViTinhs> dviTinh = new ArrayList<>();
        if (thuocs.getDonViXuatLeMaDonViTinh() != null && thuocs.getDonViXuatLeMaDonViTinh() > 0) {
            Optional<DonViTinhs> byId = donViTinhsRepository.findById(thuocs.getDonViXuatLeMaDonViTinh());
            if (byId.isPresent()) {
                byId.get().setFactor(1);
                byId.get().setGiaBan(thuocs.getGiaBanLe());
                byId.get().setGiaNhap(thuocs.getGiaNhap());
                dviTinh.add(byId.get());
                thuocs.setTenDonViTinhXuatLe(byId.get().getTenDonViTinh());
            }
        }
        if (thuocs.getDonViThuNguyenMaDonViTinh() != null && thuocs.getDonViThuNguyenMaDonViTinh() > 0 && !thuocs.getDonViThuNguyenMaDonViTinh().equals(thuocs.getDonViXuatLeMaDonViTinh())) {
            Optional<DonViTinhs> byId = donViTinhsRepository.findById(thuocs.getDonViThuNguyenMaDonViTinh());
            if (byId.isPresent()) {
                byId.get().setFactor(thuocs.getHeSo());
                byId.get().setGiaBan(thuocs.getGiaBanLe().multiply(BigDecimal.valueOf(thuocs.getHeSo())));
                byId.get().setGiaNhap(thuocs.getGiaNhap().multiply(BigDecimal.valueOf(thuocs.getHeSo())));
                dviTinh.add(byId.get());
                thuocs.setTenDonViTinhThuNguyen(byId.get().getTenDonViTinh());
            }
        }
        thuocs.setListDonViTinhs(dviTinh);
        //fill vi tri tu/kho
        if (thuocs.getIdWarehouseLocation() != null && thuocs.getIdWarehouseLocation() > 0) {
            Optional<WarehouseLocation> byId = warehouseLocationRepository.findById(thuocs.getIdWarehouseLocation());
            if (byId.isPresent()) {
                thuocs.setTenViTri(byId.get().getNameWarehouse());
            }
        }
        if (thuocs.getNhomThuocMaNhomThuoc() != null) {
            Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(thuocs.getNhomThuocMaNhomThuoc());
            byIdNt.ifPresent(nhomThuocs -> thuocs.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
        }
        InventoryReq inventoryReq = new InventoryReq();
        inventoryReq.setDrugID(thuocs.getId());
        inventoryReq.setDrugStoreID(thuocs.getNhaThuocMaNhaThuoc());
        inventoryReq.setRecordStatusID(RecordStatusContains.ACTIVE);
        Optional<Inventory> inventory = inventoryRepository.searchDetail(inventoryReq);
        inventory.ifPresent(thuocs::setInventory);

        List<ReplaceGoodsAndBundleGoods> replaceGoods = replaceGoodsAndBundleGoodsRepository.findReplaceGoodsAndBundleGoodsByDrugStoreCodeAndDrugIdMapAndTypeId(storeCode, thuocs.getId(), GoodsTypeMap.REPLACE_GOOD);
        for (ReplaceGoodsAndBundleGoods replaceGood : replaceGoods) {
            Optional<Thuocs> obj = hdrRepo.findById(replaceGood.getDrugId());
            if(obj.isPresent()){
                replaceGood.setMaThuoc(obj.get().getMaThuoc());
                replaceGood.setTenThuoc(obj.get().getTenThuoc());
            }
        }
        List<ReplaceGoodsAndBundleGoods> bundleGoods = replaceGoodsAndBundleGoodsRepository.findReplaceGoodsAndBundleGoodsByDrugStoreCodeAndDrugIdMapAndTypeId(storeCode, thuocs.getId(), GoodsTypeMap.BUNDLE_GOOD);
        for (ReplaceGoodsAndBundleGoods bundleGood : bundleGoods) {
            Optional<Thuocs> obj = hdrRepo.findById(bundleGood.getDrugId());
            if(obj.isPresent()){
                bundleGood.setMaThuoc(obj.get().getMaThuoc());
                bundleGood.setTenThuoc(obj.get().getTenThuoc());
            }
        }
        thuocs.setReplaceGoods(replaceGoods);
        thuocs.setBundleGoods(bundleGoods);
        return thuocs;
    }

    @Override
    public Boolean updateDrugPrice(ThuocsReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Optional<Thuocs> optional = hdrRepo.findById(req.getId());
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            var drug = optional.get();
            var settings = userInfo.getApplicationSettings();
            var settingOwnerPrices = settings.stream().filter(x -> x.getSettingKey().equals("SETTING_OWNER_PRICES")).findFirst();
            // nếu nhà thuốc là nhà con và thiết lập khác biệt giá thì cập nhật giá bán lẻ trên thuốc của nhà con
            if(settingOwnerPrices.isPresent() && settingOwnerPrices.get().getActivated()
                    && (!userInfo.getNhaThuoc().getMaNhaThuoc().equals(userInfo.getNhaThuoc().getMaNhaThuocCha()))){
                if (drug.getDonViThuNguyenMaDonViTinh().equals(req.getMaDonViTinhDaChon()) && drug.getHeSo() != 0) {
                    req.setGiaBanLe(req.getGiaBanLe().divide(BigDecimal.valueOf(drug.getHeSo()), RoundingMode.HALF_UP));
                }
                List<String> storeCodes = new ArrayList<>();
                storeCodes.add(userInfo.getNhaThuoc().getMaNhaThuoc());
                inventoryRepository.updateOutPriceInventory(drug.getId(), storeCodes, req.getGiaBanLe(), req.getGiaBanLe(), Date.from(Instant.now()));
                //BackgroundServiceJobHelper.EnqueueCreateInitialInventoryReceiptNote(drugStoreCode, drug.ThuocId);
                return true;
            }
            if (drug.getDonViThuNguyenMaDonViTinh().equals(req.getMaDonViTinhDaChon()) && drug.getHeSo() != 0) {
                drug.setGiaBanLe(req.getGiaBanLe().divide(BigDecimal.valueOf(drug.getHeSo()), RoundingMode.HALF_UP));
                drug.setGiaNhap(req.getGiaNhap().divide(BigDecimal.valueOf(drug.getHeSo()), RoundingMode.HALF_UP));
                drug.setGiaBanBuon(req.getGiaBanBuon().setScale(0, RoundingMode.HALF_UP));
            }
            else {
                drug.setGiaBanLe(req.getGiaBanLe());
                drug.setGiaNhap(req.getGiaNhap());
                drug.setGiaBanBuon(req.getGiaBanBuon().multiply(drug.getHeSo() > 0 ? BigDecimal.valueOf(drug.getHeSo()) : BigDecimal.ONE));
            }
            hdrRepo.save(drug);
            return true;
        }
    }

    @Override
    public Boolean updateDrugPriceForChildStore(ThuocsReq req) throws Exception {
        var result = true;
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        if(req.getStoreCodes().isEmpty()) return false;
        if (req.getDonViThuNguyenMaDonViTinh().equals(req.getMaDonViTinhDaChon()) && req.getHeSo() != 0) {
            req.setGiaBanLe(req.getGiaBanLe().divide(BigDecimal.valueOf(req.getHeSo()), RoundingMode.HALF_UP));
            req.setGiaNhap(req.getGiaNhap().divide(BigDecimal.valueOf(req.getHeSo()), RoundingMode.HALF_UP));
        }
        if(req.getIsUpdateInPrice() && req.getGiaNhap().compareTo(BigDecimal.ZERO) > 0){
            inventoryRepository.updateInPriceInventory(req.getId(), req.getStoreCodes(), req.getGiaNhap(), Date.from(Instant.now()));
        } else if (!req.getIsUpdateInPrice() && req.getGiaBanLe().compareTo(BigDecimal.ZERO) > 0) {
            inventoryRepository.updateOutPriceInventory(req.getId(), req.getStoreCodes(), req.getGiaBanLe(), req.getGiaBanLe(), Date.from(Instant.now()));
        }
        return result;
    }

    @Override
    public Boolean saveDraftListDrug(DraftListDrugReq req) throws Exception {
        var result = true;
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        var storeCode = userInfo.getNhaThuoc().getMaNhaThuocCha();
        List<DraftListDrug> drugExit = draftListDrugRepository.findByDrugId(req.getDrugId());
        List<Thuocs> drugs = hdrRepo.findByNhaThuocMaNhaThuocAndReferenceId(storeCode, req.getDrugId());
        if (!drugs.isEmpty()) {
            boolean canChangeUnit = canChangeUnits(storeCode, drugs.get(0).getId(), req.getRetailUnitId(), req.getUnitId());
            if (!canChangeUnit) {
                throw new Exception("Bạn không thể cập nhật với đơn vị tính này.");
            }
        }

        List<Thuocs> checkExitNameDrug = hdrRepo.findByNhaThuocMaNhaThuocAndTenThuocContainingAndReferenceIdNotAndRecordStatusIdNot(
                storeCode, req.getDrugName(), req.getDrugId(), RecordStatusContains.DELETED_FOREVER);

        if (!checkExitNameDrug.isEmpty()) {
            throw new Exception("Tên thuốc đã tồn tại.");
        }

        DraftListDrug draftListDrug = new DraftListDrug();
        BeanUtils.copyProperties(req, draftListDrug, "id");
        if (!drugExit.isEmpty()) {
            draftListDrug.setId(drugExit.get(0).getId());
            draftListDrug = draftListDrugRepository.save(draftListDrug);
        } else {
            draftListDrug.setStoreCode(storeCode);
            draftListDrug = draftListDrugRepository.save(draftListDrug);
        }

        if (draftListDrug.getId() > 0) {
            if (!drugs.isEmpty()) {
                drugs.forEach(x -> {
                    x.setGiaNhap(req.getNoteType().equals(ENoteType.Delivery) ? BigDecimal.ZERO : req.getInPrice());
                    x.setGiaBanLe(req.getOutPrice());
                    x.setGiaBanBuon(req.getOutBatchPrice());
                    x.setHeSo(req.getFactors());
                    x.setTenThuoc(req.getDrugName());
                    x.setThongTin(req.getDecscription());
                    x.setBarCode(req.getBarcode());
                    x.setNhomThuocMaNhomThuoc(req.getGroupId());
                    x.setDonViThuNguyenMaDonViTinh(req.getUnitId());
                    x.setDonViXuatLeMaDonViTinh(req.getRetailUnitId());
                    x.setProductTypeId(req.getProductTypeId());
                });
                hdrRepo.saveAll(drugs);
            }
        }
        return result;
    }

    //endregion

    //region Private Methods
    private List<Object[]> convertToExcelModel(List<Thuocs> data, String[] rowsName, boolean duLieuLoi) {
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;

        objs = new Object[rowsName.length];
        objs[0] = "Code";
        objs[1] = "GroupName";
        objs[2] = "Name";
        objs[3] = "Information";
        objs[4] = "InPrice";
        objs[5] = "RetailOutPrice";
        objs[6] = "Scorable";
        objs[7] = "MoneyToOneScoreRate";
        objs[8] = "DiscountByRevenue";
        objs[9] = "Discount";
        objs[10] = "BatchOutPrice";
        objs[11] = "RetailUnit";
        objs[12] = "Unit";
        objs[13] = "WarningQuantity";
        objs[14] = "Factors";
        objs[15] = "InitValue";
        objs[16] = "InitPrice";
        objs[17] = "Barcode";
        objs[18] = "SerialNumber";
        objs[19] = "ExpiredDate";
        objs[20] = "DosageForms";
        objs[21] = "RegisteredNo";
        objs[22] = "ActiveSubstance";
        objs[23] = "Contents";
        objs[24] = "PackingWay";
        objs[25] = "CountryOfManufacturer";
        objs[26] = "Manufacturer";
        objs[27] = "Importers";
        objs[28] = "SmallestPackingUnit";
        objs[29] = "DeclaredPrice";
        objs[30] = "ConnectivityCode";
        objs[31] = "ProductTypeId";
        objs[32] = "OrganizeDeclaration";
        objs[33] = "CountryRegistration";
        objs[34] = "AddressRegistration";
        objs[35] = "AddressManufacture";
        objs[36] = "Classification";
        objs[37] = "Identifier";
        objs[38] = "ForWholesale";
        objs[39] = "SaleDescription";
        objs[40] = "StorageConditions";
        objs[41] = "StorageLocation";
        objs[42] = "Result";
        dataList.add(objs);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < data.size(); i++) {
            Thuocs thuoc = data.get(i);
            objs = new Object[rowsName.length];
            objs[0] = thuoc.getMaThuoc();
            objs[1] = thuoc.getNhomThuocMaNhomThuoc() != null ? thuoc.getTenNhomThuoc() : "";
            objs[2] = thuoc.getTenThuoc();
            objs[3] = thuoc.getThongTin();
            objs[4] = decimalFormat.format(thuoc.getGiaNhap());
            objs[5] = decimalFormat.format(thuoc.getGiaBanLe());
            objs[6] = thuoc.getScorable() ? "1" : "0";
            objs[7] = thuoc.getScorable() ?
                    (thuoc.getGiaBanLe().compareTo(BigDecimal.ZERO) > 0 ?
                            String.valueOf(thuoc.getMoneyToOneScoreRate().divide(thuoc.getGiaBanLe(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP)) :
                            "0") : "";
            objs[8] = thuoc.getDiscountByRevenue() ? "1" : "0";
            objs[9] = thuoc.getDiscount() != null ? String.valueOf(thuoc.getDiscount()) : "";
            objs[10] = thuoc.getGiaBanBuon() != null ? decimalFormat.format(thuoc.getGiaBanBuon()) : "0";
            objs[11] = thuoc.getTenDonViTinhXuatLe();
            objs[12] = thuoc.getTenDonViTinhThuNguyen();
            objs[13] = thuoc.getGioiHan();
            objs[14] = thuoc.getHeSo() != null ? decimalFormat.format(thuoc.getHeSo()): "1";
            objs[15] = thuoc.getSoDuDauKy() != null ? decimalFormat.format(thuoc.getSoDuDauKy()) : "0";
            objs[16] = thuoc.getGiaDauKy() != null ? decimalFormat.format(thuoc.getGiaDauKy()) : "0";
            objs[17] = thuoc.getBarCode();
            objs[18] = thuoc.getSerialNumber();
            objs[19] = thuoc.getHanDung() != null ? dateFormat.format(thuoc.getHanDung()) : "";
            objs[20] = thuoc.getDosageForms();
            objs[21] = thuoc.getRegisteredNo();
            objs[22] = thuoc.getActiveSubstance();
            objs[23] = thuoc.getContents();
            objs[24] = thuoc.getQuyCachDongGoi();
            objs[25] = thuoc.getCountryOfManufacturer();
            objs[26] = thuoc.getManufacturer();
            objs[27] = thuoc.getImporters();
            objs[28] = thuoc.getSmallestPackingUnit();
            objs[29] = thuoc.getDeclaredPrice() != null ? decimalFormat.format(thuoc.getDeclaredPrice()) : "0";
            objs[30] = thuoc.getConnectivityCode();
            objs[31] = thuoc.getProductTypeId() != null ? String.valueOf(thuoc.getProductTypeId()) : "";
            objs[32] = thuoc.getOrganizeDeclaration();
            objs[33] = thuoc.getCountryRegistration();
            objs[34] = thuoc.getAddressRegistration();
            objs[35] = thuoc.getAddressManufacture();
            objs[36] = thuoc.getClassification();
            objs[37] = thuoc.getIdentifier();
            objs[38] = thuoc.getForWholesale() != null ? String.valueOf(thuoc.getForWholesale()) : "";
            objs[39] = thuoc.getSaleDescription();
            objs[40] = thuoc.getStorageConditions();
            objs[41] = thuoc.getStorageLocation();
            objs[42] = duLieuLoi ? "thuocs.getResult()" : "";
            dataList.add(objs);
        }

        return dataList;
    }

    @Override
    public List<dataBarcode> getDataBarcode(HashMap<String, Object> hashMap) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null) {
            throw new Exception("Bad request.");
        }
        Long idPhieu = hashMap.get("idPhieu") != null ? FileUtils.safeToLong(hashMap.get("idPhieu")) : null;
        Long loaiPhieu = hashMap.get("loaiPhieu") != null ? FileUtils.safeToLong(hashMap.get("loaiPhieu")) : null;
        Long idNhomThuoc = hashMap.get("idNhomThuoc") != null ? FileUtils.safeToLong(hashMap.get("idNhomThuoc")) : null;
        List<dataBarcode> barcodeDataList = new ArrayList<>();
        if (loaiPhieu != null && loaiPhieu == 1L && idPhieu != null) {
            List<PhieuNhapChiTiets> phieuNhapChiTiets = phieuNhapChiTietsRepository.findByPhieuNhapMaPhieuNhapAndRecordStatusId(idPhieu, RecordStatusContains.ACTIVE);
            if (!phieuNhapChiTiets.isEmpty()) {
                for (PhieuNhapChiTiets chiTiet : phieuNhapChiTiets) {
                    Optional<Thuocs> optional = Optional.ofNullable(this.detail(chiTiet.getThuocThuocId()));
                    optional.ifPresent(thuocs -> barcodeDataList.add(createDataBarcode(thuocs)));
                }
            }
        } else if (loaiPhieu != null && loaiPhieu == 2L && idPhieu != null) {
            List<PhieuXuatChiTiets> phieuXuatChiTiets = phieuXuatChiTietsRepository.findAllByPhieuXuatMaPhieuXuatAndRecordStatusId(idPhieu, RecordStatusContains.ACTIVE);
            if (!phieuXuatChiTiets.isEmpty()) {
                for (PhieuXuatChiTiets chiTiet : phieuXuatChiTiets) {
                    Optional<Thuocs> optional = Optional.ofNullable(this.detail(chiTiet.getThuocThuocId()));
                    optional.ifPresent(thuocs -> barcodeDataList.add(createDataBarcode(thuocs)));
                }
            }
        }
        if (idNhomThuoc != null) {
            List<Thuocs> thuocList = hdrRepo.findAllByNhomThuocMaNhomThuoc(idNhomThuoc);
            if (!thuocList.isEmpty()) {
                thuocList.forEach(thuoc -> barcodeDataList.add(createDataBarcode(thuoc)));
            }
        }
        return barcodeDataList.stream()
                .filter(item -> item.getMaVach() != null && !item.getMaVach().isEmpty())
                .collect(Collectors.toList());
    }

    private dataBarcode createDataBarcode(Thuocs thuocs) {
        dataBarcode barcodeData = new dataBarcode();
        barcodeData.setMaThuoc(thuocs.getMaThuoc());
        barcodeData.setTenThuoc(thuocs.getTenThuoc());
        barcodeData.setGiaBanLe(thuocs.getGiaBanLe());
        barcodeData.setGiaBanBuon(thuocs.getGiaBanBuon());
        barcodeData.setMaVach(thuocs.getBarCode());
        if (thuocs.getDonViXuatLeMaDonViTinh() != null) {
            Optional<DonViTinhs> donViTinhLe = donViTinhsRepository.findById(thuocs.getDonViXuatLeMaDonViTinh());
            donViTinhLe.ifPresent(donViTinh -> barcodeData.setDonViTinhLe(donViTinh.getTenDonViTinh()));
        }
        if (thuocs.getDonViThuNguyenMaDonViTinh() != null) {
            Optional<DonViTinhs> donViTinhBuon = donViTinhsRepository.findById(thuocs.getDonViThuNguyenMaDonViTinh());
            donViTinhBuon.ifPresent(donViTinh -> barcodeData.setDonViTinhBuon(donViTinh.getTenDonViTinh()));
        }
        return barcodeData;
    }

    @Override
    public ReportTemplateResponse preview(List<dataBarcode> listReq) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null) {
            throw new Exception("Bad request.");
        }
        String templatePath = "/maVachLieuDung/";
        Integer checkType = 0;
        for (dataBarcode data : listReq) {
            data.setTenNhaThuoc(data.getKhongInTenNhaThuoc() ? null : userInfo.getNhaThuoc().getTenNhaThuoc());
            data.setDiaChiNhaThuoc(userInfo.getNhaThuoc().getDiaChi());
            Optional<ConfigTemplate> configTemplates = null;
            configTemplates = configTemplateRepository.findByMaNhaThuocAndPrintTypeAndMaLoaiAndType(userInfo.getNhaThuoc().getMaNhaThuoc(), data.getLoaiIn(), Long.valueOf(ENoteType.MedicineBarcodeSample), checkType);
            if (!configTemplates.isPresent()) {
                configTemplates = configTemplateRepository.findByPrintTypeAndMaLoaiAndType(data.getLoaiIn(), Long.valueOf(ENoteType.MedicineBarcodeSample), checkType);
            }
            if (configTemplates.isPresent()) {
                templatePath += configTemplates.get().getTemplateFileName();
            }
            try (InputStream templateInputStream = FileUtils.getInputStreamByFileName(templatePath)) {
                return FileUtils.convertDocxToPdf(templateInputStream, data, data.getMaVach(), null, null, listReq);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Page<Thuocs> searchPageSell(ThuocsReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        req.setNhaThuocMaNhaThuocCha(userInfo.getNhaThuoc().getMaNhaThuocCha());
        if (req.getDataDelete() != null) {
            req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
        }
        Page<Thuocs> thuocs = searchPageSell(req, pageable);
        thuocs.getContent().forEach(item -> {
            if (item.getNhomThuocMaNhomThuoc() != null) {
                Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
                byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
            }
            if (req.getTypeService() != null && req.getTypeService() == 0) { //kiểm tra nếu là thuốc thì mới fill dữ liệu bên dưới
                if (item.getDonViThuNguyenMaDonViTinh() != null) {
                    Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
                    byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhThuNguyen(donViTinhs.getTenDonViTinh()));
                }
                if (item.getDonViXuatLeMaDonViTinh() != null) {
                    Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
                    byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhXuatLe(donViTinhs.getTenDonViTinh()));
                }
                if (item.getIdWarehouseLocation() != null) {
                    Optional<WarehouseLocation> byIdNt = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
                    byIdNt.ifPresent(warehouseLocations -> item.setTenViTri(warehouseLocations.getNameWarehouse()));
                }
            }
            Optional<ConnectivityDrug> connectivityDrug = connectivityDrugRepository.findByDrugIdAndDrugStoreIdAndRecordStatusId(item.getId(), item.getNhaThuocMaNhaThuoc(), RecordStatusContains.ACTIVE);
            if(connectivityDrug.isPresent()){
                item.setConnectivityDrugBaId(connectivityDrug.get().getId());
            }
        });
        return thuocs;
    }

    public Page<Thuocs> searchPageSell(ThuocsReq param, Pageable pageable) {
        List<Thuocs> result1 = hdrRepo.findByCondition(param);
        List<Thuocs> result2 = hdrRepo.findBySampleNoteDetail(param);

        Set<Thuocs> combinedResults = new LinkedHashSet<>();
        combinedResults.addAll(result1);
        combinedResults.addAll(result2);

        List<Thuocs> paginatedList = new ArrayList<>(combinedResults);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), paginatedList.size());

        Page<Thuocs> page = new PageImpl<>(paginatedList.subList(start, end), pageable, combinedResults.size());
        return page;
    }


    @Override
    public Process importExcel(MultipartFile file) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        Supplier<Thuocs> thuocsSupplier = Thuocs::new;
        InputStream inputStream = file.getInputStream();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            List<String> propertyNames = Arrays.asList("maThuoc", "tenNhomThuoc", "tenThuoc", "thongTin"
                    , "giaNhap", "giaBanLe", "scorable", "moneyToOneScoreRate", "discountByRevenue", "discount", "giaBanBuon", "tenDonViTinhXuatLe",
                    "tenDonViTinhThuNguyen", "gioiHan", "heSo", "soDuDauKy", "giaDauKy", "barCode", "serialNumber", "hanDung", "dosageForms", "registeredNo", "activeSubstance", "contents", "packingWay", "countryOfManufacturer", "manufacturer",
                    "importers", "smallestPackingUnit", "declaredPrice", "connectivityCode", "productTypeId", "organizeDeclaration", "countryRegistration", "addressRegistration", "addressManufacture",
                    "classification", "identifier", "forWholesale", "saleDescription",
                    "storageConditions", "storageLocation");
            List<Thuocs> thuocs = new ArrayList<>(handleImportExcel(workbook, propertyNames,thuocsSupplier, 2));
            return pushToKafka(thuocs);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Process pushToKafka(List<Thuocs> thuocs) throws Exception {
        int size = thuocs.size();
        int index = 1;
        UUID uuid = UUID.randomUUID();
        String bathKey = uuid.toString();
        Profile userInfo = this.getLoggedUser();
        Process process = kafkaProducer.createProcess(bathKey, userInfo.getNhaThuoc().getMaNhaThuoc(), new Gson().toJson(thuocs), new Date(),size);
        for(Thuocs bs :thuocs){
            Optional<DonViTinhs> dvtThuNguyen = donViTinhsRepository.findByTenDonViTinhAndMaNhaThuoc(bs.getTenDonViTinhThuNguyen(), this.getLoggedUser().getNhaThuoc().getMaNhaThuoc());
            Optional<DonViTinhs> dvtLe = donViTinhsRepository.findByTenDonViTinhAndMaNhaThuoc(bs.getTenDonViTinhXuatLe(), this.getLoggedUser().getNhaThuoc().getMaNhaThuoc());
            if (dvtThuNguyen.isPresent()) {
                bs.setDonViThuNguyenMaDonViTinh(dvtThuNguyen.get().getId());
                bs.setDonViXuatLeMaDonViTinh(dvtLe.get().getId());
                bs.setFlag(false);
                bs.setGroupIdMapping(0L);
            }
            String key = bs.getMaThuoc();
            WrapData data = new WrapData();
            data.setBatchKey(bathKey);
            data.setCode(ImportConstant.THUOC);
            data.setSendDate(new Date());
            data.setData(bs);
            data.setTotal(size);
            data.setIndex(index++);
            data.setProfile(this.getLoggedUser());
            kafkaProducer.createProcessDtl(process, data);
            kafkaProducer.sendInternal(topicName, key, new Gson().toJson(data));
        }
        return process;
    }

    private boolean canChangeUnits(String storeCode, Long prodId, Long retailUnitId, Long unitId) throws Exception {
        if (retailUnitId <= 0) return false;

        List<Long> validRecordStatusIds = Arrays.asList(
                RecordStatusContains.ACTIVE,
                RecordStatusContains.ARCHIVED
        );

        // Truy vấn các đơn vị tính từ PhieuXuatChiTiet
        List<PhieuXuatChiTiets> dItems = phieuXuatChiTietsRepository.findByPhieuXuatMaPhieuXuatInAndThuocThuocIdAndRecordStatusIdIn(
                phieuXuatsRepository.findByMaLoaiXuatNhapNotAndRecordStatusIdIn(ENoteType.InitialInventory.longValue(), validRecordStatusIds)
                        .stream().map(PhieuXuats::getId).collect(Collectors.toList()),
                prodId,
                validRecordStatusIds
        );

        // Truy vấn các đơn vị tính từ PhieuNhapChiTiet
        List<PhieuNhapChiTiets> rItems = phieuNhapChiTietsRepository.findByPhieuNhapMaPhieuNhapInAndThuocThuocIdAndRecordStatusIdIn(
                phieuNhapsRepository.findByLoaiXuatNhapMaLoaiXuatNhapNotAndRecordStatusIdIn(ENoteType.InitialInventory.longValue(), validRecordStatusIds)
                        .stream().map(PhieuNhaps::getId).collect(Collectors.toList()),
                prodId,
                validRecordStatusIds
        );

        // Lấy danh sách các đơn vị tính đã sử dụng
        List<Long> usedUnitIds = Stream.concat(
                dItems.stream().map(PhieuXuatChiTiets::getDonViTinhMaDonViTinh),
                rItems.stream().map(PhieuNhapChiTiets::getDonViTinhMaDonViTinh)
        ).distinct().toList();

        if (usedUnitIds.isEmpty()) return true;

        if (usedUnitIds.size() > 1) return usedUnitIds.contains(retailUnitId) && usedUnitIds.contains(unitId);

        return usedUnitIds.contains(retailUnitId) || usedUnitIds.contains(unitId);
    }

    public boolean checkUnitTran(Long id) throws Exception {
        boolean result = true;
        List<Long> units = new ArrayList<>();

        List<Long> unitReceipt = phieuNhapChiTietsRepository.findByThuocThuocId(id).stream()
                .map(PhieuNhapChiTiets::getDonViTinhMaDonViTinh)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        List<Long> unitDelivery = phieuXuatChiTietsRepository.findByThuocThuocId(id).stream()
                .map(PhieuXuatChiTiets::getDonViTinhMaDonViTinh)
                .distinct()
                .toList();

        if (!unitReceipt.isEmpty()) {
            units.addAll(unitReceipt);
        }
        if (!unitDelivery.isEmpty()) {
            units.addAll(unitDelivery);
        }

        if (!units.isEmpty()) {
            List<Long> unitFinal = units.stream().distinct().toList();
            if (unitFinal.size() > 1) {
                return false;
            }
        }
        return result;
    }

    //endregion
}
