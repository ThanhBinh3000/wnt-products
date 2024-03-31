package vn.com.gsoft.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "PhieuKiemKeChiTiets")
public class PhieuKiemKeChiTiets extends BaseEntity{
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "Thuoc_ThuocId")
    private Integer thuocThuocId;
    @Column(name = "TonKho")
    private BigDecimal tonKho;
    @Column(name = "ThucTe")
    private BigDecimal thucTe;
    @Column(name = "PhieuKiemKe_MaPhieuKiemKe")
    private Integer phieuKiemKeMaPhieuKiemKe;
    @Column(name = "DonGia")
    private BigDecimal donGia;
    @Column(name = "SoLo")
    private String soLo;
    @Column(name = "HanDung")
    private Date hanDung;
    @Column(name = "ArchiveDrugId")
    private Integer archiveDrugId;
    @Column(name = "ArchivedId")
    private Integer archivedId;
    @Column(name = "ReferenceId")
    private Integer referenceId;
    @Column(name = "StoreId")
    private Integer storeId;
    @Column(name = "IsProdRef")
    private Boolean isProdRef;
}

