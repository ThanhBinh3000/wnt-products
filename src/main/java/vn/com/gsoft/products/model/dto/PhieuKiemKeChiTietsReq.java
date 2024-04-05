package vn.com.gsoft.products.model.dto;

import jakarta.persistence.Column;
import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PhieuKiemKeChiTietsReq extends BaseRequest {
    private Long thuocThuocId;
    private BigDecimal tonKho;
    private BigDecimal thucTe;
    private Integer phieuKiemKeMaPhieuKiemKe;
    private BigDecimal donGia;
    private String soLo;
    private Date hanDung;
    private Long archiveDrugId;
    private Long archivedId;
    private Long referenceId;
    private Long storeId;
    private Boolean isProdRef;
}
