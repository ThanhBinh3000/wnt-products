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
@Table(name = "PhieuXuatChiTiets")
public class PhieuXuatChiTiets extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "PhieuXuat_MaPhieuXuat")
    private Long phieuXuatMaPhieuXuat;
    @Column(name = "NhaThuoc_MaNhaThuoc")
    private String nhaThuocMaNhaThuoc;
    @Column(name = "Thuoc_ThuocId")
    private Long thuocThuocId;
    @Column(name = "DonViTinh_MaDonViTinh")
    private Long donViTinhMaDonViTinh;
    @Column(name = "ChietKhau")
    private Double chietKhau;
    @Column(name = "GiaXuat")
    private Double giaXuat;
    @Column(name = "SoLuong")
    private Double soLuong;
    @Column(name = "Option1")
    private String option1;
    @Column(name = "Option2")
    private String option2;
    @Column(name = "Option3")
    private String option3;
    @Column(name = "RefConnectivityCode")
    private String refConnectivityCode;
    @Column(name = "PreQuantity")
    private String preQuantity;
    @Column(name = "IsReceiptDrugPriceRefGenerated")
    private Boolean isReceiptDrugPriceRefGenerated;
    @Column(name = "RetailQuantity")
    private Double retailQuantity;
    @Column(name = "HandledStatusId")
    private Long handledStatusId;
    @Column(name = "RetailPrice")
    private Double retailPrice;
    @Column(name = "RequestUpdateFromBkgService")
    private Boolean requestUpdateFromBkgService;
    @Column(name = "ReduceNoteItemIds")
    private String reduceNoteItemIds;
    @Column(name = "ReduceQuantity")
    private Double reduceQuantity;
    @Column(name = "IsModified")
    private Boolean isModified;
    @Column(name = "ItemOrder")
    private Integer itemOrder;
    @Column(name = "ArchiveDrugId")
    private Long archiveDrugId;
    @Column(name = "ArchiveUnitId")
    private Long archiveUnitId;
    @Column(name = "PreRetailQuantity")
    private Double preRetailQuantity;
    @Column(name = "BatchNumber")
    private String batchNumber;
    @Column(name = "ExpiredDate")
    private Date expiredDate;
    @Column(name = "ExpirySetAuto")
    private Boolean expirySetAuto;
    @Column(name = "ReferenceId")
    private Long referenceId;
    @Column(name = "ArchivedId")
    private Long archivedId;
    @Column(name = "StoreId")
    private Long storeId;
    @Column(name = "ConnectivityStatusId")
    private Long connectivityStatusId;
    @Column(name = "ConnectivityResult")
    private String connectivityResult;
    @Column(name = "VAT")
    private Double vat;
    @Column(name = "Reason")
    private String reason;
    @Column(name = "Solution")
    private String solution;
    @Column(name = "Notes")
    private String notes;
    @Column(name = "LockReGenReportData")
    private Boolean lockReGenReportData;
    @Column(name = "IsProdRef")
    private Boolean isProdRef;
    @Column(name = "NegativeRevenue")
    private Boolean negativeRevenue;
    @Column(name = "Revenue")
    private Double revenue;
    @Column(name = "RefPrice")
    private Double refPrice;
    @Column(name = "Usage")
    private String usage;
    @Column(name = "OutOwnerPriceChild")
    private Double outOwnerPriceChild;

    @Transient
    private String imageThumbData;
    @Transient
    private String imagePreviewData;
    @Transient
    private Thuocs thuocs;
    @Transient
    private String donViTinhMaDonViTinhText;
    @Transient
    private String maThuocText;
    @Transient
    private String tenThuocText;
    @Transient
    private Long soPhieuXuat;
    @Transient
    private Date ngayXuat;
    @Transient
    private Double vatPhieuXuat;
    @Transient
    private Double debtPaymentAmount;
    @Transient
    private Long maLoaiXuatNhap;
    @Transient
    private String maLoaiXuatNhapText;
    @Transient
    private Long khachHangMaKhachHang;
    @Transient
    private String khachHangMaKhachHangText;
    @Transient
    private String createdByUserText;
    @Transient
    private Long targetStoreId;
    @Transient
    private String targetStoreText;
    @Transient
    private Long nhaCungCapMaNhaCungCap;
    @Transient
    private String nhaCungCapMaNhaCungCapText;

}

