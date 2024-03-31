package vn.com.gsoft.products.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.WarehouseLocation;
import vn.com.gsoft.products.model.dto.WarehouseLocationReq;
import vn.com.gsoft.products.repository.WarehouseLocationRepository;
import vn.com.gsoft.products.service.WarehouseLocationService;

@Service
@Log4j2
public class WarehouseLocationServiceImpl extends BaseServiceImpl<WarehouseLocation, WarehouseLocationReq,Long> implements WarehouseLocationService {

	private WarehouseLocationRepository hdrRepo;

	@Autowired
	public WarehouseLocationServiceImpl(WarehouseLocationRepository hdrRepo) {
		super(hdrRepo);
		this.hdrRepo = hdrRepo;
	}
}
