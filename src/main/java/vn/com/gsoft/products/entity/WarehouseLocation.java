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
@Table(name = "WarehouseLocation")
public class WarehouseLocation extends BaseEntity {
    @Id
    @Column(name = "Id")
    private Long id;
    @Column(name = "Code")
    private String code;
    @Column(name = "NameWarehouse")
    private String nameWarehouse;
    @Column(name = "StoreCode")
    private String storeCode;
    @Column(name = "Descriptions")
    private String descriptions;
}

