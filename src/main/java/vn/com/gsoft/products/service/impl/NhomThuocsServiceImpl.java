package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.NhomThuocsRepository;
import vn.com.gsoft.products.service.NhomThuocsService;

import java.util.Optional;

@Service
@Log4j2
public class NhomThuocsServiceImpl extends BaseServiceImpl<NhomThuocs, NhomThuocsReq,Long> implements NhomThuocsService {

	private NhomThuocsRepository hdrRepo;
	@Autowired
	public NhomThuocsServiceImpl(NhomThuocsRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}

	@Override
	public NhomThuocs create(NhomThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		NhomThuocs hdr = new NhomThuocs();
		BeanUtils.copyProperties(req, hdr, "id");
		if(hdr.getRecordStatusId() == null){
			req.setRecordStatusId(RecordStatusContains.ACTIVE);
		}
		hdr.setMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		return hdrRepo.save(hdr);
	}

	@Override
	public NhomThuocs update(NhomThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		Optional<NhomThuocs> optional = hdrRepo.findById(req.getId());
		if (optional.isEmpty()) {
			throw new Exception("Không tìm thấy dữ liệu.");
		}
		NhomThuocs hdr = optional.get();
		BeanUtils.copyProperties(req, hdr, "id");
		if(hdr.getRecordStatusId() == null){
			hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
		}
		hdr.setMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		return hdrRepo.save(hdr);
	}
}
