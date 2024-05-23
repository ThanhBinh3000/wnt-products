package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.entity.PhieuDuTruChiTiet;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PhieuDuTruReq extends BaseRequest {
    private Integer soPhieu;
    private Date ngayTao;
    private Long createdByUserId;
    private BigDecimal tongTien;
    private String maNhaThuoc;
    private Long recordStatusID;
    private Long supplierId;
    private String linkShare;

    private List<PhieuDuTruChiTiet> chiTiets;

}
