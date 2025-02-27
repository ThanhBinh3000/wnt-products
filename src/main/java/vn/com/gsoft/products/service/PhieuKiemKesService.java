package vn.com.gsoft.products.service;


import vn.com.gsoft.products.entity.PhieuKiemKeChiTiets;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.ReportTemplateResponse;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface PhieuKiemKesService extends BaseService<PhieuKiemKes, PhieuKiemKesReq, Long> {


    PhieuKiemKes canKho(PhieuKiemKesReq idSearchReq) throws Exception;

    Boolean checkThuocTonTaiKiemKe(Long thuocThuocId) throws Exception;

    List<PhieuKiemKeChiTiets> checkBienDong(Long id) throws Exception;

    List<Thuocs> colectionNotInKiemKe(Date fromDate, Date toDate) throws Exception;

    PhieuKiemKeChiTiets updateHanDung(Long id,Double donGia, String soLo, Date hanDung) throws Exception;

    PhieuKiemKes deleteChiTiet(Long id) throws Exception;

    ReportTemplateResponse preview(HashMap<String, Object> hashMap) throws Exception;
}