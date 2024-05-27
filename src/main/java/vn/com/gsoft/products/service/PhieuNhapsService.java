package vn.com.gsoft.products.service;


import jakarta.transaction.Transactional;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.PhieuNhaps;
import vn.com.gsoft.products.model.dto.PhieuNhapsReq;

public interface PhieuNhapsService extends BaseService<PhieuNhaps, PhieuNhapsReq, Long> {
    PhieuNhaps init(Long maLoaiXuatNhap, Long id) throws Exception;
    @Transactional
    PhieuNhaps createByPhieuKiemKes(PhieuKiemKes e) throws Exception;
    @Transactional
    PhieuNhaps updateByPhieuKiemKes(PhieuKiemKes e) throws Exception;
}