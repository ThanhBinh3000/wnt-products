package vn.com.gsoft.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SampleNote")
public class SampleNote extends BaseEntity{
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "NoteName")
    private String noteName;
    @Column(name = "Barcode")
    private String barcode;
    @Column(name = "Description")
    private String description;
    @Column(name = "RecordStatusID")
    private Integer recordStatusID;
    @Column(name = "CreatedDateTime")
    private Date createdDateTime;
    @Column(name = "CreatedByUserID")
    private Integer createdByUserID;
    @Column(name = "ModifiedDateTime")
    private Date modifiedDateTime;
    @Column(name = "ModifiedByUserID")
    private Integer modifiedByUserID;
    @Column(name = "DrugStoreID")
    private String drugStoreID;
    @Column(name = "StoreId")
    private Integer storeId;
    @Column(name = "Amount")
    private BigDecimal amount;
    @Column(name = "PatientId")
    private Integer patientId;
    @Column(name = "DoctorId")
    private Integer doctorId;
    @Column(name = "DoctorComments")
    private String doctorComments;
    @Column(name = "NoteDate")
    private Date noteDate;
    @Column(name = "IdExamination")
    private Integer idExamination;
    @Column(name = "TypeId")
    private String typeId;
    @Column(name = "IsConnect")
    private Boolean isConnect;
    @Column(name = "ResultConnect")
    private String resultConnect;
    @Column(name = "CodeConnect")
    private String codeConnect;
    @Column(name = "StatusConnect")
    private Integer statusConnect;
    @Column(name = "NoteNumber")
    private Integer noteNumber;
    @Column(name = "ConnectDate")
    private Date connectDate;
    @Column(name = "NoteCheckSum")
    private String noteCheckSum;
    @Column(name = "FormOfTreatment")
    private Integer formOfTreatment;
    @Column(name = "TypeSampleNote")
    private Integer typeSampleNote;
    @Column(name = "ReferenceId")
    private Integer referenceId;
}
