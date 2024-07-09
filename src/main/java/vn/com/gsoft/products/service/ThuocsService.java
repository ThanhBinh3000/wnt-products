package vn.com.gsoft.products.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.com.gsoft.products.entity.ReportTemplateResponse;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;
import vn.com.gsoft.products.model.dto.dataBarcode;

import java.util.HashMap;
import java.util.List;

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

    HashMap<Integer, Double> getTotalInventory(InventoryReq inventoryReq) throws Exception;

    List<dataBarcode> getDataBarcode(HashMap<String, Object> hashMap) throws Exception;

    ReportTemplateResponse preview(List<dataBarcode> dataBarcodes) throws Exception;
}
