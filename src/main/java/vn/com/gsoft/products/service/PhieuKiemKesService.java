package vn.com.gsoft.products.service;


import vn.com.gsoft.products.entity.PhieuKiemKeChiTiets;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;

import java.util.List;

public interface PhieuKiemKesService extends BaseService<PhieuKiemKes, PhieuKiemKesReq, Long> {


    PhieuKiemKes canKho(PhieuKiemKesReq idSearchReq) throws Exception;

    Boolean checkThuocTonTaiKiemKe(Long thuocThuocId) throws Exception;

    List<PhieuKiemKeChiTiets> checkBienDong(Long id) throws Exception;
}