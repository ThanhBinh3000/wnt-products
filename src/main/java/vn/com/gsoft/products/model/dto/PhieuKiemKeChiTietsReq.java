package vn.com.gsoft.products.model.dto;

import jakarta.persistence.Column;
import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PhieuKiemKeChiTietsReq extends BaseRequest {
    private Integer thuocThuocId;
    private BigDecimal tonKho;
    private BigDecimal thucTe;
    private Integer phieuKiemKeMaPhieuKiemKe;
    private BigDecimal donGia;
    private String soLo;
    private Date hanDung;
    private Integer archiveDrugId;
    private Integer archivedId;
    private Integer referenceId;
    private Integer storeId;
    private Boolean isProdRef;
}
