package vn.com.gsoft.products.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ProductTypes")
public class ProductTypes extends BaseEntity {
    @Id
    @Column(name = "Id")
    private Long id;
    @Column(name = "Name")
    private String name;
    @Column(name = "DisplayName")
    private String displayName;
    @Column(name = "Visible")
    private Boolean visible;
}

