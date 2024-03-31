package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.PhieuDuTruChiTiet;
import vn.com.gsoft.products.model.dto.PhieuDuTruChiTietReq;
import vn.com.gsoft.products.repository.PhieuDuTruChiTietRepository;
import vn.com.gsoft.products.service.PhieuDuTruChiTietService;


@Service
@Log4j2
public class PhieuDuTruChiTietServiceImpl extends BaseServiceImpl<PhieuDuTruChiTiet, PhieuDuTruChiTietReq,Long> implements PhieuDuTruChiTietService {

	private PhieuDuTruChiTietRepository hdrRepo;
	@Autowired
	public PhieuDuTruChiTietServiceImpl(PhieuDuTruChiTietRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}

}
