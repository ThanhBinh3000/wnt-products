package vn.com.gsoft.products.service.impl;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.*;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.*;
import vn.com.gsoft.products.model.system.PaggingReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.repository.feign.InventoryFeign;
import vn.com.gsoft.products.service.ConnectivityDrugService;
import vn.com.gsoft.products.service.ThuocsService;
import vn.com.gsoft.products.util.system.ExportExcel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class ConnectivityDrugServiceImpl extends BaseServiceImpl<ConnectivityDrug, ConnectivityDrugReq, Long> implements ConnectivityDrugService {

    private ConnectivityDrugRepository hdrRepo;

    @Autowired
    public ConnectivityDrugServiceImpl(ConnectivityDrugRepository hdrRepo) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
    }

    @Autowired
    public DonViTinhsRepository donViTinhsRepository;

    @Autowired
    public ThuocsRepository thuocsRepository;

    //#region Override Methods
    @Override
    public Page<ConnectivityDrug> searchPage(ConnectivityDrugReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setDrugStoreId(AppConstants.ConnectivityStoreID);
        req.setForWholesale(getLoggedUser().getNhaThuoc().getIsGeneralPharmacy());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        return hdrRepo.searchPage(req, pageable);
    }

    @Override
    public List<ConnectivityDrug> searchList(ConnectivityDrugReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        req.setDrugStoreId(AppConstants.ConnectivityStoreID);
        req.setForWholesale(getLoggedUser().getNhaThuoc().getIsGeneralPharmacy());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        return hdrRepo.searchList(req);
    }

    @Override
    public Page<ConnectivityDrug> colectionPageThuocLienThong(ConnectivityDrugReq objReq) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        updateThuocLienThongChuoiCoSo(userInfo.getNhaThuoc().getMaNhaThuoc());

        List<String> maNhaThuocs = new ArrayList<>();
        maNhaThuocs.add(userInfo.getNhaThuoc().getMaNhaThuoc());
        if (!Objects.equals(userInfo.getNhaThuoc().getMaNhaThuoc(), userInfo.getNhaThuoc().getMaNhaThuocCha())) {
            maNhaThuocs.add(userInfo.getNhaThuoc().getMaNhaThuocCha());
        }
        List<Thuocs> thuocs = thuocsRepository.findByTenThuocContainingIgnoreCaseAndRecordStatusIdAndNhaThuocMaNhaThuocInOrderByTenThuoc(objReq.getTextSearch() == null ? "" : objReq.getTextSearch(), RecordStatusContains.ACTIVE, maNhaThuocs);

        List<ConnectivityDrug> connectivityDrugs = hdrRepo.findByDrugStoreIdAndRecordStatusId(userInfo.getNhaThuoc().getMaNhaThuoc(), RecordStatusContains.ACTIVE);
        List<Long> connectivityDrugIds;
        switch (objReq.getLoaiThuocLienThong()) {
            case LoaiThuocLienThongConstant.CHUA_LIEN_THONG:
                connectivityDrugIds = connectivityDrugs.stream().map(ConnectivityDrug::getDrugId).toList();
                thuocs = thuocs.stream()
                        .filter(x -> !connectivityDrugIds.contains(x.getId()))
                        .filter(x -> !Objects.equals(x.getConnectivityStatusId(), ConnectivityStatusConstant.Connected))
                        .toList();
                break;
            case LoaiThuocLienThongConstant.DA_LIEN_THONG_QUOC_GIA:
                connectivityDrugs = connectivityDrugs.stream()
                        .filter(x -> x.getConnectivityId() != null && x.getConnectivityId().contains("DQG"))
                        .toList();
                connectivityDrugIds = connectivityDrugs.stream().map(ConnectivityDrug::getDrugId).toList();
                thuocs = thuocs.stream()
                        .filter(x -> connectivityDrugIds.contains(x.getId()))
                        .filter(x -> Objects.equals(x.getConnectivityStatusId(), ConnectivityStatusConstant.Connected))
                        .toList();
                break;
            case LoaiThuocLienThongConstant.DA_LIEN_THONG_CO_SO:
                connectivityDrugs = connectivityDrugs.stream()
                        .filter(x -> x.getConnectivityId() != null && !x.getConnectivityId().contains("DQG"))
                        .toList();
                connectivityDrugIds = connectivityDrugs.stream().map(ConnectivityDrug::getDrugId).toList();
                thuocs = thuocs.stream()
                        .filter(x -> connectivityDrugIds.contains(x.getId()))
                        .toList();
                break;
            case LoaiThuocLienThongConstant.TAT_CA:
            default:
                break;
        }

        // Convert từ list sang page và fill dữ liệu vào ConnectivityDrugRes model
        PageRequest pageRequest = PageRequest.of(objReq.getPaggingReq().getPage(), objReq.getPaggingReq().getLimit());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), thuocs.size());
        List<Thuocs> sublistThuoc = thuocs.subList(start, end);
        List<ConnectivityDrug> subListRes = new ArrayList<>();

        List<ConnectivityDrug> finalConnectivityDrugs = connectivityDrugs;
        sublistThuoc.forEach(thuoc -> {
            // Fill data thuốc
            ConnectivityDrug item = new ConnectivityDrug();
            item.setDrugId(thuoc.getId());
            item.setMaThuoc(thuoc.getMaThuoc());
            item.setTenThuoc(thuoc.getTenThuoc());
            item.setConnectivityDrugID(thuoc.getConnectivityDrugID());
            item.setConnectivityCode(thuoc.getConnectivityCode());
            item.setConnectivityDrugFactor(thuoc.getConnectivityDrugFactor());
            item.setConnectivityTypeId(thuoc.getConnectivityTypeId());
            item.setRetailUnitId(thuoc.getDonViXuatLeMaDonViTinh());
            if (item.getRetailUnitId() != null) {
                Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(thuoc.getDonViXuatLeMaDonViTinh());
                byIdNt.ifPresent(donViTinhs -> item.setRetailUnitName(donViTinhs.getTenDonViTinh()));
            }

            // Fill data connect
            Optional<ConnectivityDrug> connectivityDrugOptional = finalConnectivityDrugs.stream().filter(x -> x.getDrugId().equals(thuoc.getId())).findFirst();
            if(connectivityDrugOptional.isPresent()){
                ConnectivityDrug connectivityDrug = connectivityDrugOptional.get();
                item.setId(connectivityDrug.getId());
                item.setConnectivityId(connectivityDrug.getConnectivityId());
                item.setName(connectivityDrug.getName());
                item.setUnitName(connectivityDrug.getUnitName());
                item.setManufacturer(connectivityDrug.getManufacturer());
                item.setContents(connectivityDrug.getContents());
                item.setPackingWay(connectivityDrug.getPackingWay());
                item.setRegisteredNo(connectivityDrug.getRegisteredNo());
            }
            subListRes.add(item);
        });

        return new PageImpl<>(subListRes, pageRequest, thuocs.size());
    }

    @Override
    public ConnectivityDrug create(ConnectivityDrugReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        ConnectivityDrug connectivityDrug = new ConnectivityDrug();

        req.setConnectivityTypeId((req.getConnectivityId() != null && req.getConnectivityId().contains("DQG")) ? ConnectivityTypeConstant.NationalDB : ConnectivityTypeConstant.LocalDb);
        if(Objects.equals(req.getConnectivityTypeId(), ConnectivityTypeConstant.LocalDb)){
            req.setCode(req.getMaThuoc());
            req.setName(req.getTenThuoc());
            req.setConnectivityStatusId(ConnectivityStatusConstant.NotConnected);
            req.setConnectivityResult("");
            req.setConnectivityId("");
        }
        else {
            req.setConnectivityStatusId(ConnectivityStatusConstant.Connected);
            req.setConnectivityDateTime(new Date());
            req.setConnectivityResult("Đã liên thông với thuốc trên CSDL quốc gia");
        }

        BeanUtils.copyProperties(req, connectivityDrug);
        connectivityDrug.setDrugStoreId(userInfo.getNhaThuoc().getMaNhaThuoc());
        connectivityDrug.setDrugId(req.getDrugId());
        connectivityDrug.setStoreId(userInfo.getNhaThuoc().getId());
        connectivityDrug.setRecordStatusId(RecordStatusContains.ACTIVE);
        connectivityDrug.setCreated(new Date());
        connectivityDrug.setCreatedByUserId(userInfo.getId());
        connectivityDrug = hdrRepo.save(connectivityDrug);

        updateThongTinThuocLienThong(connectivityDrug);

        return connectivityDrug;
    }

    @Override
    public ConnectivityDrug update(ConnectivityDrugReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<ConnectivityDrug> optional = hdrRepo.findById(req.getId());
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }

        ConnectivityDrug connectivityDrug = optional.get();

        req.setConnectivityTypeId((req.getConnectivityId() != null && req.getConnectivityId().contains("DQG")) ? ConnectivityTypeConstant.NationalDB : ConnectivityTypeConstant.LocalDb);
        if(Objects.equals(req.getConnectivityTypeId(), ConnectivityTypeConstant.LocalDb)){
            req.setCode(req.getMaThuoc());
            req.setName(req.getTenThuoc());
            req.setConnectivityStatusId(ConnectivityStatusConstant.NotConnected);
            req.setConnectivityResult("");
            req.setConnectivityId("");
        }
        else {
            req.setConnectivityStatusId(ConnectivityStatusConstant.Connected);
            req.setConnectivityDateTime(new Date());
            req.setConnectivityResult("Đã liên thông với thuốc trên CSDL quốc gia");
        }

        BeanUtils.copyProperties(req, connectivityDrug, "id");
        connectivityDrug.setDrugStoreId(userInfo.getNhaThuoc().getMaNhaThuoc());
        connectivityDrug.setDrugId(req.getDrugId());
        connectivityDrug.setStoreId(userInfo.getNhaThuoc().getId());
        connectivityDrug.setRecordStatusId(RecordStatusContains.ACTIVE);
        connectivityDrug.setModified(new Date());
        connectivityDrug.setModifiedByUserId(userInfo.getId());
        connectivityDrug = hdrRepo.save(connectivityDrug);

        updateThongTinThuocLienThong(connectivityDrug);

        return connectivityDrug;
    }

    @Override
    public ConnectivityDrug detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<ConnectivityDrug> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        return optional.get();
    }

    @Override
    public ConnectivityDrug detailThuocLienThong(Long drugId) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        ConnectivityDrug result = new ConnectivityDrug();

        Optional<Thuocs> thuocsOptional = thuocsRepository.findByIdAndRecordStatusId(drugId, RecordStatusContains.ACTIVE);
        if(thuocsOptional.isPresent()) {
            Optional<ConnectivityDrug> connectivityDrugOptional = hdrRepo.findByDrugIdAndDrugStoreIdAndRecordStatusId(drugId, userInfo.getNhaThuoc().getMaNhaThuoc(), RecordStatusContains.ACTIVE).stream().findFirst();
            connectivityDrugOptional.ifPresent(connectivityDrug -> BeanUtils.copyProperties(connectivityDrug, result));

            Thuocs thuocs = thuocsOptional.get();
            result.setDrugId(thuocs.getId());
            result.setMaThuoc(thuocs.getMaThuoc());
            result.setTenThuoc(thuocs.getTenThuoc());
            result.setConnectivityDrugID(thuocs.getConnectivityDrugID());
            result.setConnectivityCode(thuocs.getConnectivityCode());
            result.setConnectivityDrugFactor(thuocs.getConnectivityDrugFactor());
            result.setConnectivityTypeId(thuocs.getConnectivityTypeId());
            result.setRetailUnitId(thuocs.getDonViXuatLeMaDonViTinh());
            if (result.getRetailUnitId() != null) {
                Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(thuocs.getDonViXuatLeMaDonViTinh());
                byIdNt.ifPresent(donViTinhs -> result.setRetailUnitName(donViTinhs.getTenDonViTinh()));
            }
        }

        return result;
    }

    @Override
    public boolean delete(Long drugId) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        // Xóa bản ghi liên kết với nhà thuốc hiện tại
        List<ConnectivityDrug> connectivityDrugs = hdrRepo.findByDrugIdAndDrugStoreId(drugId, userInfo.getNhaThuoc().getMaNhaThuoc());
        connectivityDrugs.forEach(connectivityDrug -> {
            connectivityDrug.setConnectivityStatusId(ConnectivityStatusConstant.NotConnected);
            connectivityDrug.setRecordStatusId(RecordStatusContains.DELETED);
        });

        // Nếu trong chuỗi cơ sở không có bản ghi nào liên kết với dược quốc gia thì update lại thông tin thuốc
        connectivityDrugs = hdrRepo.findByConnectivityIdContainingIgnoreCaseAndDrugIdAndRecordStatusId("DQG", drugId, RecordStatusContains.ACTIVE);
        if (connectivityDrugs.isEmpty()) {
            Optional<Thuocs> thuocsOptional = thuocsRepository.findById(drugId);
            if (thuocsOptional.isPresent()) {
                Thuocs thuocs = thuocsOptional.get();
                thuocs.setConnectivityStatusId(ConnectivityStatusConstant.NotConnected);
                thuocs.setConnectivityCode(null);
                thuocs.setConnectivityDrugID(0L);
                thuocsRepository.save(thuocs);
            }
        }
        return true;
    }
    //#endregion Override Methods

    //#region Private Methods
    private void updateThongTinThuocLienThong(ConnectivityDrug result) throws Exception {
        Long groupIdMapping = 0L;
        Optional<Thuocs> thuocGroupIdMappingOptional = thuocsRepository.findByNhaThuocMaNhaThuocAndFlagAndMaThuoc("DQG", true, result.getCode());
        if (thuocGroupIdMappingOptional.isPresent()) {
            groupIdMapping = thuocGroupIdMappingOptional.get().getGroupIdMapping();
        }
        if (result.getId() > 0) {
            Optional<Thuocs> thuocsOptional = thuocsRepository.findById(result.getDrugId());
            if (thuocsOptional.isPresent()) {
                Thuocs thuoc = thuocsOptional.get();
                thuoc.setModified(new Date());
                thuoc.setModifiedByUserId(result.getModifiedByUserId());
                thuoc.setConnectivityDrugID(result.getId());
                thuoc.setConnectivityDrugFactor(BigDecimal.ZERO);
                thuoc.setConnectivityTypeId(result.getConnectivityTypeId());
                thuoc.setConnectivityCode(result.getConnectivityId());
                thuoc.setManufacturer(result.getManufacturer());
                thuoc.setPackingWay(result.getPackingWay());
                thuoc.setRegisteredNo(result.getRegisteredNo());
                thuoc.setActiveSubstance(result.getActiveSubstance());
                thuoc.setConnectivityResult(result.getConnectivityResult());
                thuoc.setConnectivityStatusId(result.getConnectivityStatusId());
                thuoc.setDosageForms(result.getDosageForms());
                thuoc.setSmallestPackingUnit(result.getSmallestPackingUnit());
                thuoc.setImporters(result.getImporters());
                if (Objects.equals(result.getConnectivityTypeId(), ConnectivityTypeConstant.LocalDb)) {
                    thuoc.setConnectivityTypeId(ConnectivityTypeConstant.LocalDb);
                    thuoc.setConnectivityDrugID(result.getId());
                    thuoc.setConnectivityDrugFactor(BigDecimal.ONE);
                    thuoc.setFlag(groupIdMapping > 0L);
                    thuocsRepository.save(thuoc);
                } else {
                    thuoc.setRegisteredNo(result.getRegisteredNo());
                    thuoc.setActiveSubstance(result.getActiveSubstance());
                    thuoc.setPackingWay(result.getPackingWay());
                    thuoc.setQuyCachDongGoi(result.getPackingWay());
                    thuoc.setFlag(groupIdMapping > 0L);
                    thuoc.setGroupIdMapping(groupIdMapping);
                    thuocsRepository.save(thuoc);

                    // Update thông tin liên kết cho các nhà thuốc khác
                    List<ConnectivityDrug> connectivityDrugs = hdrRepo.findByDrugIdAndConnectivityTypeIdAndConnectivityIdAndDrugStoreIdNot(result.getDrugId(), ConnectivityTypeConstant.NationalDB, result.getConnectivityId(), result.getDrugStoreId());
                    connectivityDrugs.forEach(connectivityDrug -> {
                        connectivityDrug.setCountryId(result.getCountryId());
                        connectivityDrug.setCountryOfManufacturer(result.getCountryOfManufacturer());
                        connectivityDrug.setManufacturer(result.getManufacturer());
                        connectivityDrug.setPackingWay(result.getPackingWay());
                        connectivityDrug.setRegisteredNo(result.getRegisteredNo());
                        connectivityDrug.setUnitName(result.getUnitName());
                        connectivityDrug.setContents(result.getContents());
                        connectivityDrug.setActiveSubstance(result.getActiveSubstance());
                        connectivityDrug.setConnectivityResult(result.getConnectivityResult());
                        connectivityDrug.setConnectivityStatusId(result.getConnectivityStatusId());
                        connectivityDrug.setDosageForms(result.getDosageForms());
                        connectivityDrug.setSmallestPackingUnit(result.getSmallestPackingUnit());
                        connectivityDrug.setDeclaredPrice(result.getDeclaredPrice());
                        connectivityDrug.setWholesalePrice(result.getWholesalePrice());
                        connectivityDrug.setImporters(result.getImporters());
                        connectivityDrug.setOrganizeDeclaration(result.getOrganizeDeclaration());
                        connectivityDrug.setCountryRegistration(result.getCountryRegistration());
                        connectivityDrug.setAddressRegistration(result.getAddressRegistration());
                        connectivityDrug.setAddressManufacture(result.getAddressManufacture());
                        connectivityDrug.setClassification(result.getClassification());
                        connectivityDrug.setIdentifier(result.getIdentifier());
                        connectivityDrug.setForWholesale(result.getForWholesale());
                        hdrRepo.save(connectivityDrug);
                    });
                }
            }
        }
    }

    private void updateThuocLienThongChuoiCoSo(String maNhaThuoc) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        List<String> maNhaThuocs = new ArrayList<>();
        maNhaThuocs.add(userInfo.getNhaThuoc().getMaNhaThuoc());
        if (!Objects.equals(userInfo.getNhaThuoc().getMaNhaThuoc(), userInfo.getNhaThuoc().getMaNhaThuocCha())) {
            maNhaThuocs.add(userInfo.getNhaThuoc().getMaNhaThuocCha());
        }
        List<Thuocs> thuocs = thuocsRepository.findByRecordStatusIdAndNhaThuocMaNhaThuocInOrderByTenThuoc(RecordStatusContains.ACTIVE, maNhaThuocs);

        List<ConnectivityDrug> connectivityDrugs = hdrRepo.findByDrugStoreIdAndRecordStatusId(userInfo.getNhaThuoc().getMaNhaThuoc(), RecordStatusContains.ACTIVE);

        // Lọc ra danh sách thuốc chưa liên kết
        thuocs = thuocs.stream()
                .filter(x -> connectivityDrugs.stream().noneMatch(y -> y.getDrugId().equals(x.getId())))
                .toList();

        thuocs.forEach(thuoc -> {
            Optional<ConnectivityDrug> connectivityDrugOptional = hdrRepo.findByIdAndRecordStatusId(thuoc.getConnectivityDrugID(), RecordStatusContains.ACTIVE);
            if (thuoc.getConnectivityDrugID() != null && thuoc.getConnectivityDrugID() > 0 && connectivityDrugOptional.isPresent()) {
                ConnectivityDrug connectivityDrug = connectivityDrugOptional.get();
                connectivityDrug.setDrugStoreId(maNhaThuoc);
                connectivityDrug.setDrugId(thuoc.getId());
                connectivityDrug.setStoreId(userInfo.getNhaThuoc().getId());
                if (Objects.equals(connectivityDrug.getConnectivityTypeId(), ConnectivityTypeConstant.LocalDb)) {
                    connectivityDrug.setConnectivityStatusId(ConnectivityStatusConstant.NotConnected);
                    connectivityDrug.setConnectivityResult("");
                    connectivityDrug.setConnectivityId("");
                } else {
                    connectivityDrug.setCreated(new Date());
                    connectivityDrug.setConnectivityDateTime(new Date());
                }
                hdrRepo.save(connectivityDrug);
            }
        });
    }
    //#endregion Private Methods
}
