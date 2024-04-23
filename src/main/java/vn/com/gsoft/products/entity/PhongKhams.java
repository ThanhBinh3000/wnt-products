package vn.com.gsoft.products.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PhongKhams")
public class PhongKhams extends BaseEntity {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "MaPhongKham")
    private String maPhongKham;
    @Column(name = "TenPhongKham")
    private String tenPhongKham;
    @Column(name = "MaNhaThuoc")
    private String maNhaThuoc;
    @Column(name = "Description")
    private String description;
}

