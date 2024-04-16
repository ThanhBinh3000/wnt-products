package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.DonViTinhs;
import vn.com.gsoft.products.entity.ProductTypes;
import vn.com.gsoft.products.model.dto.DonViTinhsReq;
import vn.com.gsoft.products.model.dto.ProductTypesReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.DonViTinhsRepository;
import vn.com.gsoft.products.repository.ProductTypesRepository;
import vn.com.gsoft.products.service.DonViTinhsService;
import vn.com.gsoft.products.service.ProductTypesService;

import java.util.List;

@Service
@Log4j2
public class ProductTypesServiceImpl implements ProductTypesService {

	@Autowired
	private ProductTypesRepository hdrRepo;

	@Override
	public Page<ProductTypes> searchPage(ProductTypesReq req) throws Exception {
		return null;
	}

	@Override
	public List<ProductTypes> searchList(ProductTypesReq req) throws Exception {
		return hdrRepo.searchList(req);
	}

	@Override
	public ProductTypes create(ProductTypesReq req) throws Exception {
		return null;
	}

	@Override
	public ProductTypes update(ProductTypesReq req) throws Exception {
		return null;
	}

	@Override
	public ProductTypes detail(Long id) throws Exception {
		return null;
	}

	@Override
	public boolean delete(Long id) throws Exception {
		return false;
	}

	@Override
	public boolean restore(Long id) throws Exception {
		return false;
	}

	@Override
	public boolean deleteForever(Long id) throws Exception {
		return false;
	}


	@Override
	public boolean updateStatusMulti(ProductTypesReq req) throws Exception {
		return false;
	}
}
