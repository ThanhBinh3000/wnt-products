package vn.com.gsoft.products.service;


import jakarta.servlet.http.HttpServletResponse;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.ThuocsReq;

public interface ThuocsService extends BaseService<Thuocs, ThuocsReq,Long> {
    String generateDrugCode() throws Exception;
    String generateBarCode() throws Exception;
    Thuocs uploadImage(FileDto req) throws Exception;
    void export(ThuocsReq req, HttpServletResponse response) throws Exception;
}
