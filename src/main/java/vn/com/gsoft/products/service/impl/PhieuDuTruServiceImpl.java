package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.PhieuDuTru;
import vn.com.gsoft.products.model.dto.PhieuDuTruReq;
import vn.com.gsoft.products.repository.PhieuDuTruRepository;
import vn.com.gsoft.products.service.PhieuDuTruService;


@Service
@Log4j2
public class PhieuDuTruServiceImpl extends BaseServiceImpl<PhieuDuTru, PhieuDuTruReq,Long> implements PhieuDuTruService {

	private PhieuDuTruRepository hdrRepo;
	@Autowired
	public PhieuDuTruServiceImpl(PhieuDuTruRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}

}
