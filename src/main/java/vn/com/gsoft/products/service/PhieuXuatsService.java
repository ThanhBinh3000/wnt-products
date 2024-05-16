package vn.com.gsoft.products.service;


import jakarta.transaction.Transactional;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.PhieuXuats;
import vn.com.gsoft.products.model.dto.PhieuXuatsReq;

public interface PhieuXuatsService extends BaseService<PhieuXuats, PhieuXuatsReq, Long> {

    PhieuXuats init(Long maLoaiXuatNhap, Long id) throws Exception;
    @Transactional
    PhieuXuats createByPhieuKiemKes(PhieuKiemKes e) throws Exception;
    @Transactional
    PhieuXuats updateByPhieuKiemKes(PhieuKiemKes e) throws Exception;
}