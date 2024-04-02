package vn.com.gsoft.products.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.gsoft.products.entity.WarehouseLocation;
import vn.com.gsoft.products.model.dto.WarehouseLocationReq;

import java.util.List;

@Repository
public interface WarehouseLocationRepository extends BaseRepository<WarehouseLocation, WarehouseLocationReq, Long> {
  @Query("SELECT c FROM WarehouseLocation c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.code} IS NULL OR lower(c.code) LIKE lower(concat('%',CONCAT(:#{#param.code},'%'))))"
          + " AND (:#{#param.nameWarehouse} IS NULL OR lower(c.nameWarehouse) LIKE lower(concat('%',CONCAT(:#{#param.nameWarehouse},'%'))))"
          + " AND (:#{#param.storeCode} IS NULL OR lower(c.storeCode) LIKE lower(concat('%',CONCAT(:#{#param.storeCode},'%'))))"
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
          + " AND (:#{#param.descriptions} IS NULL OR lower(c.descriptions) LIKE lower(concat('%',CONCAT(:#{#param.descriptions},'%'))))"
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
          + " ORDER BY c.id desc"
  )
  Page<WarehouseLocation> searchPage(@Param("param") WarehouseLocationReq param, Pageable pageable);
  
  
  @Query("SELECT c FROM WarehouseLocation c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.code} IS NULL OR lower(c.code) LIKE lower(concat('%',CONCAT(:#{#param.code},'%'))))"
          + " AND (:#{#param.nameWarehouse} IS NULL OR lower(c.nameWarehouse) LIKE lower(concat('%',CONCAT(:#{#param.nameWarehouse},'%'))))"
          + " AND (:#{#param.storeCode} IS NULL OR lower(c.storeCode) LIKE lower(concat('%',CONCAT(:#{#param.storeCode},'%'))))"
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
          + " AND (:#{#param.descriptions} IS NULL OR lower(c.descriptions) LIKE lower(concat('%',CONCAT(:#{#param.descriptions},'%'))))"
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
          + " ORDER BY c.id desc"
  )
  List<WarehouseLocation> searchList(@Param("param") WarehouseLocationReq param);

}
