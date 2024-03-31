package vn.com.gsoft.products.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.util.Date;

@Data
public class WarehouseLocationReq extends BaseRequest {
    private String code;
    private String nameWarehouse;
    private String storeCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date modified;
    private Integer createBy;
    private Integer modifieBy;
    private String descriptions;
}

