package vn.com.gsoft.products.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;

import java.util.HashMap;

public interface DichVuService extends BaseService<Thuocs, ThuocsReq, Long> {
    String generateDrugCode() throws Exception;

    String generateBarCode() throws Exception;

    void export(ThuocsReq req, HttpServletResponse response) throws Exception;

}
