package vn.com.gsoft.products.service;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import vn.com.gsoft.products.entity.Inventory;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;

import java.util.HashMap;
import java.util.Optional;

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

    // xem chi tiết số tồn ở các kho
    Object getDataDetailLastValueWarehouse(Long thuocId) throws Exception;
}
