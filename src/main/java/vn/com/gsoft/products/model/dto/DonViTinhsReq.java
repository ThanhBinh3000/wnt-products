package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;


@Data
public class DonViTinhsReq extends BaseRequest {
    private String tenDonViTinh;
    private String maNhaThuoc;
    private Integer referenceId;
    private Integer archivedId;
    private Integer storeId;
}

