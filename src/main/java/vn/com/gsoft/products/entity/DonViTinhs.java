package vn.com.gsoft.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long id;
    @Column(name = "TenDonViTinh")
    private String tenDonViTinh;
    @Column(name = "MaNhaThuoc")
    private String maNhaThuoc;
    @Column(name = "ReferenceId")
    private Integer referenceId;
    @Column(name = "ArchivedId")
    private Integer archivedId;
    @Column(name = "StoreId")
    private Integer storeId;
}

