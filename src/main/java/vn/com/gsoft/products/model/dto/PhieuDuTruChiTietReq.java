package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;

@Data
public class PhieuDuTruChiTietReq extends BaseRequest {
    private Integer maPhieuDuTru;
    private Integer maThuoc;
    private Integer maDonViTon;
    private BigDecimal soLuongCanhBao;
    private BigDecimal tonKho;
    private BigDecimal duTru;
    private Integer maDonViDuTru;
    private BigDecimal donGia;
}
