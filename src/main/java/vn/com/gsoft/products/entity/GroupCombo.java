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
@Table(name = "GroupCombo")
public class GroupCombo {
    @Id
    @Column(name = "Id")
    private Long id;
    @Column(name = "DrugId")
    private Long drugId;
    @Column(name = "GroupId")
    private Long groupId;
    @Column(name = "DrugStoreCode")
    private String drugStoreCode;
    @Column(name = "Created")
    private Date created;
    @Column(name = "CreateByUserId")
    private Long createByUserId;
}

