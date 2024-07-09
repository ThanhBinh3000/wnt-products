package vn.com.gsoft.products.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import vn.com.gsoft.products.model.system.BaseRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class NoteMedicalsReq extends BaseRequest {

    private Integer noteNumber;
    private Long idPatient;
    private Long idDoctor;
    private Date noteDate;
    private String includingDiseases;
    private String drugAllergy;
    private String diagnostic;
    private String conclude;
    private String storeCode;
    private Date reexaminationDate;
    private BigDecimal totalMoney;
    private Integer sickCondition;
    private String clinicalExamination;
    private Boolean isDeb;
    private String heartbeat;
    private String temperature;
    private String weight;
    private String bloodPressure;
    private String breathing;
    private String height;
    private Integer clinicCode;
    private Integer statusNote;
    private List<Integer> statusNotes;
    private Integer orderWait;
    private String reasonExamination;
    private Integer idServiceExam;
    private Integer idDiagnostic;
    private String testResults;
    private String diagnosticId;
    private String diagnosticIds;
    private String diagnosticOther;
    private Boolean isLock;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date fromDateNote;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date toDateNote;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date fromDateReExamination;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date toDateReExamination;
    private Long maNhomKhachHang;
}

