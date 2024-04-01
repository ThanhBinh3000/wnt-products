package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PhieuDuTru")
public class PhieuDuTru extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "SoPhieu")
    private Integer soPhieu;
    @Column(name = "NgayTao")
    private Date ngayTao;
    @Column(name = "TongTien")
    private BigDecimal tongTien;
    @Column(name = "MaNhaThuoc")
    private String maNhaThuoc;
    @Column(name = "SupplierId")
    private Integer supplierId;
    @Column(name = "LinkShare")
    private String linkShare;
}

