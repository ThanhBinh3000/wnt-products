package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.constant.StatusConfirmDrugContains;
import vn.com.gsoft.products.constant.TypeServiceContains;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.service.ThuocsService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class ThuocsServiceImpl extends BaseServiceImpl<Thuocs, ThuocsReq,Long> implements ThuocsService {

	private ThuocsRepository hdrRepo;

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

	@Override
	public Page<Thuocs> searchPage(ThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
		req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		req.setNhaThuocMaNhaThuocCha(userInfo.getNhaThuoc().getMaNhaThuocCha());
		if(req.getDataDelete() != null){
			req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
		}
		Page<Thuocs> thuocs = hdrRepo.searchPage(req, pageable);
		thuocs.getContent().forEach( item -> {
			if(item.getNhomThuocMaNhomThuoc()!=null){
				Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
				byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
			}
			if(req.getTypeService() == 0) { //kiểm tra nếu là thuốc thì mới fill dữ liệu bên dưới
				if(item.getDonViThuNguyenMaDonViTinh()!=null){
					Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
					byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhThuNguyen(donViTinhs.getTenDonViTinh()));
				}
				if(item.getDonViXuatLeMaDonViTinh()!=null){
					Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
					byIdNt.ifPresent(donViTinhs -> item.setTenDonViTinhXuatLe(donViTinhs.getTenDonViTinh()));
				}
				if(item.getIdWarehouseLocation() != null ){
					Optional<WarehouseLocation> byIdNt = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
					byIdNt.ifPresent(warehouseLocations -> item.setTenViTri(warehouseLocations.getNameWarehouse()));
				}
			}
		});
		return thuocs;
	}

	@Override
	public Thuocs create(ThuocsReq req) throws Exception {
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
		if(!maThuoc.isEmpty()) {
			Optional<Thuocs> byMaThuocAndRecordStatusId = hdrRepo.findByMaThuoc(maThuoc,storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
			if(byMaThuocAndRecordStatusId.isPresent()){
				throw new Exception("Đã tồn tại mã thuốc.");
			}
		}
		//check trùng tên
		if(!tenThuoc.isEmpty()) {
			Optional<Thuocs> byTenThuocAndRecordStatusId = hdrRepo.findByTenThuoc(tenThuoc,storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
			if(byTenThuocAndRecordStatusId.isPresent()){
				throw new Exception("Đã tồn tại tên thuốc.");
			}
		}
		//check trùng barcode
		if(!barCode.isEmpty()) {
			Optional<Thuocs> byBarcodeAndRecordStatusId = hdrRepo.findByBarCode(barCode,storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
			if(byBarcodeAndRecordStatusId.isPresent()){
				throw new Exception("Đã tồn tại mã vạch thuốc.");
			}
		}
		Thuocs hdr = new Thuocs();
		BeanUtils.copyProperties(req, hdr, "id");
		if(req.getRecordStatusId() == null){
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
		if(hdr.getDonViThuNguyenMaDonViTinh().equals(hdr.getDonViXuatLeMaDonViTinh())){
			hdr.setDonViThuNguyenMaDonViTinh(null);
		}
		if(hdr.getGroupIdMapping() > 0) {
			hdr.setMappingDate(new Date());
			hdr.setStatusConfirm(StatusConfirmDrugContains.MAPPEDBYSYSTEM);
		}
		else {
			if (hdr.getFlag()){
				hdr.setMappingDate(new Date());
				hdr.setStatusConfirm(StatusConfirmDrugContains.ADDNEW);
			}
		}

		hdr = hdrRepo.save(hdr);
		if(hdr.getId() > 0 && hdr.getFlag() && hdr.getGroupIdMapping() <= 0){
			hdr.setGroupIdMapping(hdr.getId());
			hdr = hdrRepo.save(hdr);
		}
		return hdr;
	}

	@Override
	public Thuocs update(ThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		Optional<Thuocs> optional = hdrRepo.findById(req.getId());
		if (optional.isEmpty()) {
			throw new Exception("Không tìm thấy dữ liệu.");
		}
		else {
			String storeCode = userInfo.getNhaThuoc().getMaNhaThuoc();
			String parentStoreCode = userInfo.getNhaThuoc().getMaNhaThuocCha() != null && !userInfo.getNhaThuoc().getMaNhaThuocCha().isEmpty()
					? userInfo.getNhaThuoc().getMaNhaThuocCha() : storeCode;
			String maThuoc = req.getMaThuoc();
			String tenThuoc = req.getTenThuoc();
			String barCode = req.getBarCode();
			//check trùng mã
			if(!optional.get().getMaThuoc().equals(maThuoc) && !maThuoc.isEmpty()) {
				Optional<Thuocs> byMaThuocAndRecordStatusId = hdrRepo.findByMaThuoc(maThuoc,storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
				if (byMaThuocAndRecordStatusId.isPresent()) {
					throw new Exception("Đã tồn tại mã thuốc.");
				}
			}
			//check trùng tên
			if(!optional.get().getTenThuoc().equals(tenThuoc) && !tenThuoc.isEmpty()) {
				Optional<Thuocs> byTenThuocAndRecordStatusId = hdrRepo.findByTenThuoc(tenThuoc,storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
				if(byTenThuocAndRecordStatusId.isPresent()){
					throw new Exception("Đã tồn tại tên thuốc.");
				}
			}
			//check trùng barcode
			if(!optional.get().getBarCode().equals(barCode) && !barCode.isEmpty()) {
				Optional<Thuocs> byBarcodeAndRecordStatusId = hdrRepo.findByBarCode(barCode,storeCode, parentStoreCode, RecordStatusContains.ACTIVE);
				if(byBarcodeAndRecordStatusId.isPresent()){
					throw new Exception("Đã tồn tại mã vạch thuốc.");
				}
			}
		}
		Thuocs hdr = optional.get();
		BeanUtils.copyProperties(req, hdr, "id");
		if(hdr.getRecordStatusId() == null){
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
		if(!req.getDonViXuatLeMaDonViTinh().equals(hdr.getDonViXuatLeMaDonViTinh())){
			hdr.setSoDuDauKy(new BigDecimal(hdr.getHeSo()).multiply(hdr.getSoDuDauKy()));
		}
		if(hdr.getDonViXuatLeMaDonViTinh().equals(hdr.getDonViThuNguyenMaDonViTinh())){
			hdr.setDonViThuNguyenMaDonViTinh(null);
		}
		hdr = hdrRepo.save(hdr);
		return hdr;
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

		while (hdrRepo.findByBarCode(barcode,storeCode, parentStoreCode, RecordStatusContains.ACTIVE).isPresent()) {
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
	public Thuocs detail(Long id) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");

		Optional<Thuocs> optional = hdrRepo.findById(id);
		if (optional.isEmpty()) {
			throw new Exception("Không tìm thấy dữ liệu.");
		}else {
			if(optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE){
				throw new Exception("Không tìm thấy dữ liệu.");
			}
		}
		Thuocs thuocs = optional.get();
		//fill dvt
		List<DonViTinhs> dviTinh = new ArrayList<>();
		if(thuocs.getDonViXuatLeMaDonViTinh() > 0){
			Optional<DonViTinhs> byId = donViTinhsRepository.findById(thuocs.getDonViXuatLeMaDonViTinh());
			if(byId.isPresent()){
				byId.get().setFactor(1);
				byId.get().setGiaBan(thuocs.getGiaBanLe());
				byId.get().setGiaNhap(thuocs.getGiaNhap());
				dviTinh.add(byId.get());
				thuocs.setTenDonViTinhXuatLe(byId.get().getTenDonViTinh());
			}
		}
		if(thuocs.getDonViThuNguyenMaDonViTinh() > 0 && !thuocs.getDonViThuNguyenMaDonViTinh().equals(thuocs.getDonViXuatLeMaDonViTinh())){
			Optional<DonViTinhs> byId = donViTinhsRepository.findById(thuocs.getDonViThuNguyenMaDonViTinh());
			if(byId.isPresent()){
				byId.get().setFactor(thuocs.getHeSo());
				byId.get().setGiaBan(thuocs.getGiaBanLe().multiply(BigDecimal.valueOf(thuocs.getHeSo())));
				byId.get().setGiaNhap(thuocs.getGiaNhap().multiply(BigDecimal.valueOf(thuocs.getHeSo())));
				dviTinh.add(byId.get());
				thuocs.setTenDonViTinhThuNguyen(byId.get().getTenDonViTinh());
			}
		}
		thuocs.setListDonViTinhs(dviTinh);
		//fill vi tri tu/kho
		if(thuocs.getIdWarehouseLocation() > 0){
			Optional<WarehouseLocation> byId = warehouseLocationRepository.findById(thuocs.getIdWarehouseLocation());
			if(byId.isPresent()){
				thuocs.setTenViTri(byId.get().getNameWarehouse());
			}
		}
		return thuocs;
	}
}
