package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SampleNoteDetailReq extends BaseRequest {
    private Integer noteID;
    private Integer drugID;
    private Integer drugUnitID;
    private String comment;
    private String drugStoreID;
    private BigDecimal quantity;
    private Integer storeId;
    private Integer batch;
    private Date fromDate;
    private Date toDate;
    private String numberOfPotionBars;
}
