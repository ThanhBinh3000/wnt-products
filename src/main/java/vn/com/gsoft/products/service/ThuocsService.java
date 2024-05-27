package vn.com.gsoft.products.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.ThuocsReq;

public interface ThuocsService extends BaseService<Thuocs, ThuocsReq, Long> {
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    Thuocs create(ThuocsReq req,String object ) throws Exception;

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    Thuocs update(ThuocsReq req, String object) throws Exception;

    String generateDrugCode() throws Exception;

    String generateBarCode() throws Exception;

    Thuocs uploadImage(FileDto req) throws Exception;

    void export(ThuocsReq req, HttpServletResponse response) throws Exception;

    Page<Thuocs> colectionPageNotInPhieuKiemKe(ThuocsReq objReq) throws Exception;

    Page<Thuocs> colectionPageHangDuTru(ThuocsReq objReq) throws Exception;
}
