package vn.com.gsoft.products.service;


import org.springframework.data.domain.Page;
import vn.com.gsoft.products.entity.ConnectivityDrug;
import vn.com.gsoft.products.model.dto.*;

public interface ConnectivityDrugService extends BaseService<ConnectivityDrug, ConnectivityDrugReq, Long> {
    Page<ConnectivityDrug> colectionPageThuocLienThong(ConnectivityDrugReq objReq) throws Exception;
    ConnectivityDrug detailThuocLienThong(Long drugId) throws Exception;
}
