package vn.com.gsoft.products.model.dto;

import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SampleNoteReq extends BaseRequest {
    private String noteName;
    private String barcode;
    private String description;
    private Integer recordStatusID;
    private Date createdDateTime;
    private Integer createdByUserID;
    private Date modifiedDateTime;
    private Integer modifiedByUserID;
    private String drugStoreID;
    private Integer storeId;
    private BigDecimal amount;
    private Integer patientId;
    private Integer doctorId;
    private String doctorComments;
    private Date noteDate;
    private Integer idExamination;
    private String typeId;
    private Boolean isConnect;
    private String resultConnect;
    private String codeConnect;
    private Integer statusConnect;
    private Integer noteNumber;
    private Date connectDate;
    private String noteCheckSum;
    private Integer formOfTreatment;
    private Integer typeSampleNote;
    private Integer referenceId;
}
