package vn.com.gsoft.products.model.dto;

import lombok.Data;

@Data
public class PhieuXuatNhapRes {
    private Long id;
    private String loaiPhieu;
    private Long soPhieu;
    private Integer soLuongThuoc;
}
