package vn.com.gsoft.products.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class dataBarcode {
    private String maThuoc;
    private String tenThuoc;
    private String donViTinhLe;
    private BigDecimal giaBanLe;
    private String donViTinhBuon;
    private BigDecimal giaBanBuon;
    private String maVach;
    private BigDecimal slTem;
    private String loaiIn;
    private String tenNhaThuoc;
    private String diaChiNhaThuoc;
    private String tenThuocMaThuoc;
    private Boolean khongInTenNhaThuoc;
    private String hoatChat;
    private String cachPha;
    private String cachUong;
}
