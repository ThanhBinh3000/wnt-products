package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.util.Date;

@Data
public class PhieuKiemKesReq extends BaseRequest {
    private Integer phieuNhapMaPhieuNhap;
    private Integer phieuXuatMaPhieuXuat;
    private String nhaThuocMaNhaThuoc;
    private Long userProfileUserId;
    private Boolean daCanKho;
    private Boolean active;
    private Integer soPhieu;
    private Long archivedId;
    private Long storeId;
    private Date archivedDate;
}
