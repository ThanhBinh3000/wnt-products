package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

@Data
public class NhomThuocsReq extends BaseRequest {

    private String tenNhomThuoc;
    private String kyHieuNhomThuoc;
    private String maNhaThuoc;
    private Boolean active;
    private Boolean referenceId;
    private Long archivedId;
    private Long storeId;
    private Long typeGroupProduct;
}
