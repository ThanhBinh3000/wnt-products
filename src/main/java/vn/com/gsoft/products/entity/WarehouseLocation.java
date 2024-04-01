package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

