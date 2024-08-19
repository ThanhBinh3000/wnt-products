package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.entity.WarehouseLocation;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.dto.WarehouseLocationReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.WarehouseLocationRepository;
import vn.com.gsoft.products.service.WarehouseLocationService;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class WarehouseLocationServiceImpl extends BaseServiceImpl<WarehouseLocation, WarehouseLocationReq,Long> implements WarehouseLocationService {

	private WarehouseLocationRepository hdrRepo;

	@Autowired
	public WarehouseLocationServiceImpl(WarehouseLocationRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}

	@Override
	public List<WarehouseLocation> searchList(WarehouseLocationReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		req.setStoreCode(userInfo.getNhaThuoc().getMaNhaThuoc());
		req.setRecordStatusId(RecordStatusContains.ACTIVE);
		return hdrRepo.searchList(req);
	}

	@Override
	public WarehouseLocation create(WarehouseLocationReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		List<WarehouseLocation> validData = hdrRepo.findAllByCodeAndNameWarehouseAndStoreCodeAndRecordStatusId(req.getCode(), req.getNameWarehouse(), req.getStoreCode(), RecordStatusContains.ACTIVE);
		if(validData.isEmpty()){
			throw new Exception("Mã và tên danh sách bị trùng, vui lòng nhập mã và tên khác");
		}
		WarehouseLocation hdr = new WarehouseLocation();
		BeanUtils.copyProperties(req, hdr, "id");
		if(hdr.getRecordStatusId() == null){
			hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
		}



		hdr.setCreated(new Date());
		hdr.setCreatedByUserId(getLoggedUser().getId());
		WarehouseLocation save = hdrRepo.save(hdr);
		return save;
	}

	@Override
	public WarehouseLocation update(WarehouseLocationReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");

		Optional<WarehouseLocation> optional = hdrRepo.findById(req.getId());
		if (optional.isEmpty()) {
			throw new Exception("Không tìm thấy dữ liệu.");
		}
		WarehouseLocation hdr = optional.get();
		if(!req.getCode().equals(hdr.getCode()) || !req.getNameWarehouse().equals(hdr.getNameWarehouse())){
			List<WarehouseLocation> validData = hdrRepo.findAllByCodeAndNameWarehouseAndStoreCodeAndRecordStatusId(req.getCode(), req.getNameWarehouse(), req.getStoreCode(), RecordStatusContains.ACTIVE);
			if(validData.isEmpty()){
				throw new Exception("Mã và tên danh sách bị trùng, vui lòng nhập mã và tên khác");
			}
		}

		BeanUtils.copyProperties(req, hdr, "id");
		if(hdr.getRecordStatusId() == null){
			hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
		}
		hdr.setModified(new Date());
		hdr.setModifiedByUserId(userInfo.getId());
		return hdrRepo.save(hdr);
	}
}
