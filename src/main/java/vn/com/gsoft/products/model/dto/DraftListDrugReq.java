package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;

@Data
public class DraftListDrugReq extends BaseRequest {

    private Long drugId;
    private String drugName;
    private Long unitId;
    private Long retailUnitId;
    private BigDecimal inPrice;
    private BigDecimal outPrice;
    private BigDecimal outBatchPrice;
    private Integer factors;
    private String decscription;
    private String storeCode;
    private String barcode;
    private Long groupId;
    private Long productTypeId;

    private Integer noteType;
}
