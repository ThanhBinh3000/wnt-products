package vn.com.gsoft.products.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import vn.com.gsoft.products.entity.Inventory;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;

import java.util.HashMap;
import java.util.Optional;

public interface ThuocsService extends BaseService<Thuocs, ThuocsReq, Long> {
    String generateDrugCode() throws Exception;

    String generateBarCode() throws Exception;

    Thuocs uploadImage(FileDto req) throws Exception;

    void export(ThuocsReq req, HttpServletResponse response) throws Exception;

    Page<Thuocs> colectionPageNotInPhieuKiemKe(ThuocsReq objReq) throws Exception;
    Page<Thuocs> colectionPageHangDuTru(ThuocsReq objReq) throws Exception;
    HashMap<Integer, Double> getTotalInventory(InventoryReq inventoryReq) throws Exception;
}
