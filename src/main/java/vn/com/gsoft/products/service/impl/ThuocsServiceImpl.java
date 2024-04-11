package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.DonViTinhs;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.entity.WarehouseLocation;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.DonViTinhsRepository;
import vn.com.gsoft.products.repository.NhomThuocsRepository;
import vn.com.gsoft.products.repository.ThuocsRepository;
import vn.com.gsoft.products.repository.WarehouseLocationRepository;
import vn.com.gsoft.products.service.ThuocsService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

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

	@Override
	public Page<Thuocs> searchPage(ThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
		req.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		if(req.getDataDelete() != null){
			req.setRecordStatusId(req.getDataDelete() ? RecordStatusContains.DELETED : RecordStatusContains.ACTIVE);
		}
		Page<Thuocs> thuocs = hdrRepo.searchPage(req, pageable);
		thuocs.getContent().forEach( item -> {
			if(item.getNhomThuocMaNhomThuoc()!=null){
				Optional<NhomThuocs> byIdNt = nhomThuocsRepository.findById(item.getNhomThuocMaNhomThuoc());
				byIdNt.ifPresent(nhomThuocs -> item.setTenNhomThuoc(nhomThuocs.getTenNhomThuoc()));
			}
			if(item.getDonViThuNguyenMaDonViTinh()!=null){
				Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViThuNguyenMaDonViTinh());
				byIdNt.ifPresent(nhomThuocs -> item.setTenDonViTinhThuNguyen(nhomThuocs.getTenDonViTinh()));
			}
			if(item.getDonViXuatLeMaDonViTinh()!=null){
				Optional<DonViTinhs> byIdNt = donViTinhsRepository.findById(item.getDonViXuatLeMaDonViTinh());
				byIdNt.ifPresent(nhomThuocs -> item.setTenDonViTinhXuatLe(nhomThuocs.getTenDonViTinh()));
			}
			if(item.getIdWarehouseLocation() != null ){
				Optional<WarehouseLocation> byIdNt = warehouseLocationRepository.findById(item.getIdWarehouseLocation());
				byIdNt.ifPresent(nhomThuocs -> item.setTenViTri(nhomThuocs.getNameWarehouse()));
			}
		});
		return thuocs;
	}

	@Override
	public Thuocs create(ThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		Thuocs hdr = new Thuocs();
		BeanUtils.copyProperties(req, hdr, "id");
		if(req.getRecordStatusId() == null){
			hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
		}
		hdr.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		hdr.setCreatedByUserId(userInfo.getId());
		hdr.setCreated(new Date());
		hdr.setCodeHash(1L);
		hdr.setNameHash(1L);
		hdr.setConnectivityCode("Fix");
		hdr.setConnectivityDrugFactor(BigDecimal.valueOf(1));
		hdr.setConnectivityDrugID(1L);
		hdr.setConnectivityStatusId(1L);
		hdr.setConnectivityTypeId(1L);
		hdr.setCountNumbers(1);
		hdr.setCountryId(1L);
		hdr.setDeclaredPrice(BigDecimal.valueOf(1));
		hdr.setDiscount(BigDecimal.valueOf(1));
		hdr.setDiscountByRevenue(false);
		hdr.setDuTru(req.getGioiHan());
		hdr.setForWholesale(1);
		hdr.setHoatDong(true);
		hdr.setIdClinic(1L);
		hdr.setIdTypeService(1L);
		hdr.setMetadataHash(1);
		hdr.setParentDrugId(1L);
		hdr.setReferenceId(1L);
		hdr.setRpMetadataHash(1);
		hdr.setSaleOff(BigDecimal.valueOf(1L));
		hdr.setSaleTypeId(1L);
		hdr.setStoreId(userInfo.getNhaThuoc().getId());
		hdr.setTypeServices(0);
		hdr.setTypeService(0);
		return hdrRepo.save(hdr);
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
		Thuocs hdr = optional.get();
		BeanUtils.copyProperties(req, hdr, "id");
		if(hdr.getRecordStatusId() == null){
			hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
		}
		hdr.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		hdr.setCreatedByUserId(userInfo.getId());
		hdr.setCreated(new Date());
		hdr.setCodeHash(1L);
		hdr.setNameHash(1L);
		hdr.setConnectivityCode("Fix");
		hdr.setConnectivityDrugFactor(BigDecimal.valueOf(1));
		hdr.setConnectivityDrugID(1L);
		hdr.setConnectivityStatusId(1L);
		hdr.setConnectivityTypeId(1L);
		hdr.setCountNumbers(1);
		hdr.setCountryId(1L);
		hdr.setDeclaredPrice(BigDecimal.valueOf(1));
		hdr.setDiscount(BigDecimal.valueOf(1));
		hdr.setDiscountByRevenue(false);
		hdr.setDuTru(req.getGioiHan());
		hdr.setForWholesale(1);
		hdr.setHoatDong(true);
		hdr.setIdClinic(1L);
		hdr.setIdTypeService(1L);
		hdr.setMetadataHash(1);
		hdr.setParentDrugId(1L);
		hdr.setReferenceId(1L);
		hdr.setRpMetadataHash(1);
		hdr.setSaleOff(BigDecimal.valueOf(1L));
		hdr.setSaleTypeId(1L);
		hdr.setStoreId(userInfo.getNhaThuoc().getId());
		hdr.setTypeServices(0);
		hdr.setTypeService(0);
		return hdrRepo.save(hdr);
	}
}
