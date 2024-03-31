package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.util.Date;

@Data
public class PhieuKiemKesReq extends BaseRequest {
    private Integer phieuNhapMaPhieuNhap;
    private Integer phieuXuatMaPhieuXuat;
    private String nhaThuocMaNhaThuoc;
    private Integer userProfileUserId;
    private Boolean daCanKho;
    private Boolean active;
    private Integer soPhieu;
    private Integer archivedId;
    private Integer storeId;
    private Date archivedDate;
}
