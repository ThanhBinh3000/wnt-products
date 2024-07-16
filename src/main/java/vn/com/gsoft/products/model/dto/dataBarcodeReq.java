package vn.com.gsoft.products.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class dataBarcodeReq {
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private BigDecimal giaBan;
    private String maVach;
    private BigDecimal slTem;
    private Long idPhieu;
    private Long loaiPhieu;
    private Long idNhomThuoc;
    private String loaiIn;
}
