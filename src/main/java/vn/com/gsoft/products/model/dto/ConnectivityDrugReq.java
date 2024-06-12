package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConnectivityDrugReq extends BaseRequest {

    private String code;
    private String name;
    private String registeredNo;
    private String activeSubstance;
    private String contents;
    private String packingWay;
    private String manufacturer;
    private String countryOfManufacturer;
    private String unitName;
    private String drugStoreId;
    private Long drugId;
    private Long connectivityStatusId;
    private Long countryId;
    private String connectivityId;
    private String connectivityResult;
    private Date connectivityDateTime;
    private Date createdDate;
    private Long storeId;
    private Long connectivityTypeId;
    private String dosageForms;
    private String smallestPackingUnit;
    private BigDecimal declaredPrice;
    private BigDecimal wholesalePrice;
    private String importers;
    private String organizeDeclaration;
    private String countryRegistration;
    private String addressRegistration;
    private String addressManufacture;
    private String classification;
    private String identifier;
    private Long prescriptionTypeId;
    private Boolean forWholesale;

    private boolean updatable;
    private String connectivityName;
    private Integer loaiThuocLienThong;
    private Long connectivityDrugId;
    private String maThuoc;
    private String tenThuoc;
}

