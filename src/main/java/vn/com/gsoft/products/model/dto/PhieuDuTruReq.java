package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PhieuDuTruReq extends BaseRequest {
    private Integer soPhieu;
    private Date ngayTao;
    private Integer createdByUserId;
    private BigDecimal tongTien;
    private String maNhaThuoc;
    private Integer recordStatusID;
    private Integer supplierId;
    private String linkShare;
}
