package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;
import vn.com.gsoft.products.repository.PhieuKiemKesRepository;
import vn.com.gsoft.products.service.PhieuKiemKesService;


@Service
@Log4j2
public class PhieuKiemKesServiceImpl extends BaseServiceImpl<PhieuKiemKes, PhieuKiemKesReq,Long> implements PhieuKiemKesService {

	private PhieuKiemKesRepository hdrRepo;
	@Autowired
	public PhieuKiemKesServiceImpl(PhieuKiemKesRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}

}
