package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.repository.NhomThuocsRepository;
import vn.com.gsoft.products.service.NhomThuocsService;

@Service
@Log4j2
public class NhomThuocsServiceImpl extends BaseServiceImpl<NhomThuocs, NhomThuocsReq,Long> implements NhomThuocsService {

	private NhomThuocsRepository hdrRepo;
	@Autowired
	public NhomThuocsServiceImpl(NhomThuocsRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}
}
