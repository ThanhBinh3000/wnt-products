package vn.com.gsoft.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.ConnectivityDrug;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.ConnectivityDrugReq;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectivityDrugRepository extends BaseRepository<ConnectivityDrug, ConnectivityDrugReq, Long> {
    @Query("SELECT c FROM ConnectivityDrug c "
            + "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.drugStoreId} IS NULL OR c.drugStoreId = :#{#param.drugStoreId})"
            + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId})"
            + " AND (:#{#param.drugId} IS NULL OR c.drugId = :#{#param.drugId})"
            + " AND (:#{#param.forWholesale} IS NULL OR c.forWholesale = :#{#param.forWholesale})"
            + " AND ((:#{#param.textSearch} IS NULL OR lower(c.code) LIKE lower(concat('%',CONCAT(:#{#param.textSearch},'%')))) "
            + " OR (:#{#param.textSearch} IS NULL OR lower(c.name) LIKE lower(concat('%',CONCAT(:#{#param.textSearch},'%')))) "
            + " OR (:#{#param.textSearch} IS NULL OR lower(c.registeredNo) LIKE lower(concat('%',CONCAT(:#{#param.textSearch},'%'))))) "
            + " ORDER BY c.id desc"
    )
    Page<ConnectivityDrug> searchPage(@Param("param") ConnectivityDrugReq param, Pageable pageable);


    @Query("SELECT c FROM ConnectivityDrug c "
            + "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.drugStoreId} IS NULL OR c.drugStoreId = :#{#param.drugStoreId})"
            + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId})"
            + " AND (:#{#param.drugId} IS NULL OR c.drugId = :#{#param.drugId})"
            + " AND (:#{#param.forWholesale} IS NULL OR c.forWholesale = :#{#param.forWholesale})"
            + " AND ((:#{#param.textSearch} IS NULL OR lower(c.code) LIKE lower(concat('%',CONCAT(:#{#param.textSearch},'%')))) "
            + " OR (:#{#param.textSearch} IS NULL OR lower(c.name) LIKE lower(concat('%',CONCAT(:#{#param.textSearch},'%')))) "
            + " OR (:#{#param.textSearch} IS NULL OR lower(c.registeredNo) LIKE lower(concat('%',CONCAT(:#{#param.textSearch},'%'))))) "
            + " ORDER BY c.id desc"
    )
    List<ConnectivityDrug> searchList(@Param("param") ConnectivityDrugReq param);

    List<ConnectivityDrug> findByConnectivityIdContainingIgnoreCaseAndDrugIdAndRecordStatusId(String connectivityId, Long drugId, Long recordStatusId);

    List<ConnectivityDrug> findByDrugIdAndDrugStoreId(Long drugId, String drugStoreId);

    List<ConnectivityDrug> findByDrugStoreIdAndRecordStatusId(String drugStoreId, Long recordStatusId);

    List<ConnectivityDrug> findByDrugIdAndConnectivityTypeIdAndConnectivityIdAndDrugStoreIdNot(Long drugId, Long connectivityTypeId, String connectivityId, String drugStoreId);


    Optional<ConnectivityDrug> findByIdAndRecordStatusId(Long id, Long recordStatusId);

    Optional<ConnectivityDrug> findByIdAndDrugStoreIdAndRecordStatusId(Long id, String drugStoreId, Long recordStatusId);

    Optional<ConnectivityDrug> findByDrugIdAndDrugStoreIdAndRecordStatusId(Long drugId, String drugStoreId, Long recordStatusId);
}
