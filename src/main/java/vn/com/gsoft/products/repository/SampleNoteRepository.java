package vn.com.gsoft.products.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.gsoft.products.entity.SampleNote;
import vn.com.gsoft.products.model.dto.SampleNoteReq;

import java.util.List;

@Repository
public interface SampleNoteRepository extends BaseRepository<SampleNote, SampleNoteReq, Long> {
    @Query("SELECT c FROM SampleNote c " +
            "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.noteName} IS NULL OR lower(c.noteName) LIKE lower(concat('%',CONCAT(:#{#param.noteName},'%'))))"
            + " AND (:#{#param.barcode} IS NULL OR lower(c.barcode) LIKE lower(concat('%',CONCAT(:#{#param.barcode},'%'))))"
            + " AND (:#{#param.description} IS NULL OR lower(c.description) LIKE lower(concat('%',CONCAT(:#{#param.description},'%'))))"
            + " AND (:#{#param.recordStatusID} IS NULL OR c.recordStatusID = :#{#param.recordStatusID}) "
            + " AND (:#{#param.createdByUserID} IS NULL OR c.createdByUserID = :#{#param.createdByUserID}) "
            + " AND (:#{#param.modifiedByUserID} IS NULL OR c.modifiedByUserID = :#{#param.modifiedByUserID}) "
            + " AND (:#{#param.drugStoreID} IS NULL OR lower(c.drugStoreID) LIKE lower(concat('%',CONCAT(:#{#param.drugStoreID},'%'))))"
            + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
            + " AND (:#{#param.amount} IS NULL OR c.amount = :#{#param.amount}) "
            + " AND (:#{#param.patientId} IS NULL OR c.patientId = :#{#param.patientId}) "
            + " AND (:#{#param.doctorId} IS NULL OR c.doctorId = :#{#param.doctorId}) "
            + " AND (:#{#param.doctorComments} IS NULL OR lower(c.doctorComments) LIKE lower(concat('%',CONCAT(:#{#param.doctorComments},'%'))))"
            + " AND (:#{#param.idExamination} IS NULL OR c.idExamination = :#{#param.idExamination}) "
            + " AND (:#{#param.typeId} IS NULL OR lower(c.typeId) LIKE lower(concat('%',CONCAT(:#{#param.typeId},'%'))))"
            + " AND (:#{#param.resultConnect} IS NULL OR lower(c.resultConnect) LIKE lower(concat('%',CONCAT(:#{#param.resultConnect},'%'))))"
            + " AND (:#{#param.codeConnect} IS NULL OR lower(c.codeConnect) LIKE lower(concat('%',CONCAT(:#{#param.codeConnect},'%'))))"
            + " AND (:#{#param.statusConnect} IS NULL OR c.statusConnect = :#{#param.statusConnect}) "
            + " AND (:#{#param.noteNumber} IS NULL OR c.noteNumber = :#{#param.noteNumber}) "
            + " AND (:#{#param.noteCheckSum} IS NULL OR lower(c.noteCheckSum) LIKE lower(concat('%',CONCAT(:#{#param.noteCheckSum},'%'))))"
            + " AND (:#{#param.formOfTreatment} IS NULL OR c.formOfTreatment = :#{#param.formOfTreatment}) "
            + " AND (:#{#param.typeSampleNote} IS NULL OR c.typeSampleNote = :#{#param.typeSampleNote}) "
            + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
            + " ORDER BY c.id desc"
    )
    Page<SampleNote> searchPage(@Param("param") SampleNoteReq param, Pageable pageable);


    @Query("SELECT c FROM SampleNote c " +
            "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.noteName} IS NULL OR lower(c.noteName) LIKE lower(concat('%',CONCAT(:#{#param.noteName},'%'))))"
            + " AND (:#{#param.barcode} IS NULL OR lower(c.barcode) LIKE lower(concat('%',CONCAT(:#{#param.barcode},'%'))))"
            + " AND (:#{#param.description} IS NULL OR lower(c.description) LIKE lower(concat('%',CONCAT(:#{#param.description},'%'))))"
            + " AND (:#{#param.recordStatusID} IS NULL OR c.recordStatusID = :#{#param.recordStatusID}) "
            + " AND (:#{#param.createdByUserID} IS NULL OR c.createdByUserID = :#{#param.createdByUserID}) "
            + " AND (:#{#param.modifiedByUserID} IS NULL OR c.modifiedByUserID = :#{#param.modifiedByUserID}) "
            + " AND (:#{#param.drugStoreID} IS NULL OR lower(c.drugStoreID) LIKE lower(concat('%',CONCAT(:#{#param.drugStoreID},'%'))))"
            + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
            + " AND (:#{#param.amount} IS NULL OR c.amount = :#{#param.amount}) "
            + " AND (:#{#param.patientId} IS NULL OR c.patientId = :#{#param.patientId}) "
            + " AND (:#{#param.doctorId} IS NULL OR c.doctorId = :#{#param.doctorId}) "
            + " AND (:#{#param.doctorComments} IS NULL OR lower(c.doctorComments) LIKE lower(concat('%',CONCAT(:#{#param.doctorComments},'%'))))"
            + " AND (:#{#param.idExamination} IS NULL OR c.idExamination = :#{#param.idExamination}) "
            + " AND (:#{#param.typeId} IS NULL OR lower(c.typeId) LIKE lower(concat('%',CONCAT(:#{#param.typeId},'%'))))"
            + " AND (:#{#param.resultConnect} IS NULL OR lower(c.resultConnect) LIKE lower(concat('%',CONCAT(:#{#param.resultConnect},'%'))))"
            + " AND (:#{#param.codeConnect} IS NULL OR lower(c.codeConnect) LIKE lower(concat('%',CONCAT(:#{#param.codeConnect},'%'))))"
            + " AND (:#{#param.statusConnect} IS NULL OR c.statusConnect = :#{#param.statusConnect}) "
            + " AND (:#{#param.noteNumber} IS NULL OR c.noteNumber = :#{#param.noteNumber}) "
            + " AND (:#{#param.noteCheckSum} IS NULL OR lower(c.noteCheckSum) LIKE lower(concat('%',CONCAT(:#{#param.noteCheckSum},'%'))))"
            + " AND (:#{#param.formOfTreatment} IS NULL OR c.formOfTreatment = :#{#param.formOfTreatment}) "
            + " AND (:#{#param.typeSampleNote} IS NULL OR c.typeSampleNote = :#{#param.typeSampleNote}) "
            + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
            + " ORDER BY c.id desc"
    )
    List<SampleNote> searchList(@Param("param") SampleNoteReq param);

}
