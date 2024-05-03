package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;

@Data
public class PhieuDuTruChiTietReq extends BaseRequest {
    private Long maPhieuDuTru;
    private Long maThuoc;
    private Long maDonViTon;
    private BigDecimal soLuongCanhBao;
    private BigDecimal tonKho;
    private BigDecimal duTru;
    private Long maDonViDuTru;
    private BigDecimal donGia;
}
