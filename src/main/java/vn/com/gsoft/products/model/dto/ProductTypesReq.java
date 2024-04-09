package vn.com.gsoft.products.model.dto;


import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;


@Data
public class ProductTypesReq extends BaseRequest {
    private String name;
    private String displayName;
    private Boolean visible;
}

