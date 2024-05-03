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
@Table(name = "PhieuKiemKeChiTiets")
public class PhieuKiemKeChiTiets extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Thuoc_ThuocId")
    private Long thuocThuocId;
    @Column(name = "TonKho")
    private BigDecimal tonKho;
    @Column(name = "ThucTe")
    private BigDecimal thucTe;
    @Column(name = "PhieuKiemKe_MaPhieuKiemKe")
    private Long phieuKiemKeMaPhieuKiemKe;
    @Column(name = "DonGia")
    private BigDecimal donGia;
    @Column(name = "SoLo")
    private String soLo;
    @Column(name = "HanDung")
    private Date hanDung;
    @Column(name = "ArchiveDrugId")
    private Long archiveDrugId;
    @Column(name = "ArchivedId")
    private Long archivedId;
    @Column(name = "ReferenceId")
    private Long referenceId;
    @Column(name = "StoreId")
    private Long storeId;
    @Column(name = "IsProdRef")
    private Boolean isProdRef;

    @Transient
    private String maThuoc;
    @Transient
    private String tenThuoc;
    @Transient
    private String tenNhomThuoc;
}

