package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DonViTinhs")
public class DonViTinhs extends BaseEntity{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "TenDonViTinh")
    private String tenDonViTinh;
    @Column(name = "MaNhaThuoc")
    private String maNhaThuoc;
    @Column(name = "ReferenceId")
    private Long referenceId;
    @Column(name = "ArchivedId")
    private Long archivedId;
    @Column(name = "StoreId")
    private Long storeId;
}

