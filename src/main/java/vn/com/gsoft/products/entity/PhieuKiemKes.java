package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer phieuNhapMaPhieuNhap;
    @Column(name = "PhieuXuat_MaPhieuXuat")
    private Integer phieuXuatMaPhieuXuat;
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
    private List<PhieuKiemKeChiTiets> chiTiets;

}

