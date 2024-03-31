package vn.com.gsoft.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "SampleNoteDetail")
public class SampleNoteDetail extends BaseEntity{
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "NoteID")
    private Integer noteID;
    @Column(name = "DrugID")
    private Integer drugID;
    @Column(name = "DrugUnitID")
    private Integer drugUnitID;
    @Column(name = "Comment")
    private String comment;
    @Column(name = "DrugStoreID")
    private String drugStoreID;
    @Column(name = "Quantity")
    private BigDecimal quantity;
    @Column(name = "StoreId")
    private Integer storeId;
    @Column(name = "Batch")
    private Integer batch;
    @Column(name = "FromDate")
    private Date fromDate;
    @Column(name = "ToDate")
    private Date toDate;
    @Column(name = "NumberOfPotionBars")
    private String numberOfPotionBars;
}

