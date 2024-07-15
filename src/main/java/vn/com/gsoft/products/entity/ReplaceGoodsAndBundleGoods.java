package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ReplaceGoodsAndBundleGoods")
public class ReplaceGoodsAndBundleGoods{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DrugId")
    private Long drugId;
    @Column(name = "DrugIdMap")
    private Long drugIdMap;
    @Column(name = "TypeId")
    private Long typeId;
    @Column(name = "DrugStoreCode")
    private String drugStoreCode;
    @Transient
    private String maThuoc;
    @Transient
    private String tenThuoc;
}

