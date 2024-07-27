package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DraftListDrug")
public class DraftListDrug extends BaseEntity {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long drugId;
    private String drugName;
    private Long unitId;
    private Long retailUnitId;
    private BigDecimal inPrice;
    private BigDecimal outPrice;
    private BigDecimal outBatchPrice;
    private Integer factors;
    private String decscription;
    private String storeCode;
    private String barcode;
    private Long groupId;
    private Long productTypeId;
}
