package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
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

import java.util.List;

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
}
