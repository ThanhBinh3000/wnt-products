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
import vn.com.gsoft.products.repository.NhomThuocsRepository;
import vn.com.gsoft.products.service.NhomThuocsService;

import java.util.Date;
import java.util.List;
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
		if(req.getRecordStatusId() == null){
			hdr.setRecordStatusId(RecordStatusContains.ACTIVE);
		}
		hdr.setMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		hdr.setCreated(new Date());
		hdr.setCreatedByUserId(userInfo.getId());
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
		hdr.setModified(new Date());
		hdr.setModifiedByUserId(userInfo.getId());
		return hdrRepo.save(hdr);
	}

	@Override
	public List<NhomThuocs> searchList(NhomThuocsReq req) throws Exception {
		Profile userInfo = this.getLoggedUser();
		if (userInfo == null)
			throw new Exception("Bad request.");
		req.setMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
		req.setRecordStatusId(RecordStatusContains.ACTIVE);
		return hdrRepo.searchList(req);
	}
}
