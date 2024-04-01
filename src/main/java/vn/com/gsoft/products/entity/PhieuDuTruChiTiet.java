package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PhieuDuTruChiTiet")
public class PhieuDuTruChiTiet extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "MaPhieuDuTru")
    private Integer maPhieuDuTru;
    @Column(name = "MaThuoc")
    private Integer maThuoc;
    @Column(name = "MaDonViTon")
    private Integer maDonViTon;
    @Column(name = "SoLuongCanhBao")
    private BigDecimal soLuongCanhBao;
    @Column(name = "TonKho")
    private BigDecimal tonKho;
    @Column(name = "DuTru")
    private BigDecimal duTru;
    @Column(name = "MaDonViDuTru")
    private Integer maDonViDuTru;
    @Column(name = "DonGia")
    private BigDecimal donGia;
}

