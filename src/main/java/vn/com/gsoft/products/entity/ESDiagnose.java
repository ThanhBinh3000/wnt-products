package vn.com.gsoft.products.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ESDiagnose")
public class ESDiagnose {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "ma_chan_doan")
    private String maChanDoan;
    @Column(name = "ten_chan_doan")
    private String tenChanDoan;
    @Column(name = "ket_luan")
    private String ketLuan;
    @Column(name = "note_id")
    private Integer noteId;
}
