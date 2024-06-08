package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.gsoft.products.model.dto.PhieuXuatNhapRes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PhieuKiemKes")
public class PhieuKiemKes extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "PhieuNhap_MaPhieuNhap")
    private Long phieuNhapMaPhieuNhap;
    @Column(name = "PhieuXuat_MaPhieuXuat")
    private Long phieuXuatMaPhieuXuat;
    @Column(name = "NhaThuoc_MaNhaThuoc")
    private String nhaThuocMaNhaThuoc;
    @Column(name = "UserProfile_UserId")
    private Long userProfileUserId;
    @Column(name = "DaCanKho")
    private Boolean daCanKho;
    @Column(name = "Active")
    private Boolean active;
    @Column(name = "SoPhieu")
    private Integer soPhieu;
    @Column(name = "ArchivedId")
    private Long archivedId;
    @Column(name = "StoreId")
    private Long storeId;
    @Column(name = "ArchivedDate")
    private Date archivedDate;
    @Transient
    private String createdByUseText;
    @Transient
    private String nhaThuocMaNhaThuocText;
    @Transient
    private List<PhieuKiemKeChiTiets> chiTiets;
    @Transient
    private List<PhieuXuatNhapRes> phieuXuatNhaps;

    //    priview
    @Transient
    private String diaChiNhaThuoc;
    @Transient
    private String sdtNhaThuoc;
}

