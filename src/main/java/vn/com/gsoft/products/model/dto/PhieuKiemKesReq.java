package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.entity.PhieuKiemKeChiTiets;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.util.Date;
import java.util.List;

@Data
public class PhieuKiemKesReq extends BaseRequest {
    private Long phieuNhapMaPhieuNhap;
    private Long phieuXuatMaPhieuXuat;
    private String nhaThuocMaNhaThuoc;
    private Long userProfileUserId;
    private Boolean daCanKho;
    private Boolean active;
    private Integer soPhieu;
    private Long archivedId;
    private Long storeId;
    private Date archivedDate;
    private List<PhieuKiemKeChiTiets> chiTiets;

    private Long thuocThuocId;

    private Date fromDate;
    private Date toDate;
    private Double donGia;
    private String soLo;
    private Date hanDung;
}
