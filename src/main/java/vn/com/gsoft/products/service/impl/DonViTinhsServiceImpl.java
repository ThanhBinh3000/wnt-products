package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.DonViTinhs;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.model.dto.DonViTinhsReq;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.DonViTinhsRepository;
import vn.com.gsoft.products.service.DonViTinhsService;

import java.util.List;

@Service
@Log4j2
public class DonViTinhsServiceImpl extends BaseServiceImpl<DonViTinhs, DonViTinhsReq,Long> implements DonViTinhsService {

	private DonViTinhsRepository hdrRepo;
	@Autowired
	public DonViTinhsServiceImpl(DonViTinhsRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}

	@Override
	public List<DonViTinhs> searchList(DonViTinhsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		req.setMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		req.setRecordStatusId(RecordStatusContains.ACTIVE);
		return hdrRepo.searchList(req);
	}
}
