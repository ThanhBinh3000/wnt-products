package vn.com.gsoft.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PhieuKiemKes")
public class PhieuKiemKes extends BaseEntity{
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "PhieuNhap_MaPhieuNhap")
    private Integer phieuNhapMaPhieuNhap;
    @Column(name = "PhieuXuat_MaPhieuXuat")
    private Integer phieuXuatMaPhieuXuat;
    @Column(name = "NhaThuoc_MaNhaThuoc")
    private String nhaThuocMaNhaThuoc;
    @Column(name = "UserProfile_UserId")
    private Integer userProfileUserId;
    @Column(name = "DaCanKho")
    private Boolean daCanKho;
    @Column(name = "Active")
    private Boolean active;
    @Column(name = "SoPhieu")
    private Integer soPhieu;
    @Column(name = "ArchivedId")
    private Integer archivedId;
    @Column(name = "StoreId")
    private Integer storeId;
    @Column(name = "ArchivedDate")
    private Date archivedDate;
}

