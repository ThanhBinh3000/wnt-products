package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConnectivityDrugRes {

    private Long drugId;
    private Long retailUnitId;
    private String drugCode;
    private String drugName;
    private String retailUnitName;

    // Thông tin thuốc LT theo danh mục quốc gia
    private Long connectivityDrugID;
    private String connectivityDrugCode;
    private String connectivityDrugName;
    private BigDecimal connectivityDrugFactor;
    private String registeredNo;
    private String packingWay;
    private String connectivityManufacturer;
    private String connectivityDrugUnitName;
    private String contents;
    private Long connectivityTypeId;
    private String activeSubstance;
    private Long countryId;
}

