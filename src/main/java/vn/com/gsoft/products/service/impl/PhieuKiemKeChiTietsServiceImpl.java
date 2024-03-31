package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.PhieuKiemKeChiTiets;
import vn.com.gsoft.products.model.dto.PhieuKiemKeChiTietsReq;
import vn.com.gsoft.products.repository.PhieuKiemKeChiTietsRepository;
import vn.com.gsoft.products.service.PhieuKiemKeChiTietsService;


@Service
@Log4j2
public class PhieuKiemKeChiTietsServiceImpl extends BaseServiceImpl<PhieuKiemKeChiTiets, PhieuKiemKeChiTietsReq, Long> implements PhieuKiemKeChiTietsService {

    private PhieuKiemKeChiTietsRepository hdrRepo;

    @Autowired
    public PhieuKiemKeChiTietsServiceImpl(PhieuKiemKeChiTietsRepository hdrRepo) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
    }

}
