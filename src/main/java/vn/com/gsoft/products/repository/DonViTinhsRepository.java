package vn.com.gsoft.products.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.gsoft.products.entity.DonViTinhs;
import vn.com.gsoft.products.model.dto.DonViTinhsReq;

import java.util.List;

@Repository
public interface DonViTinhsRepository extends BaseRepository<DonViTinhs, DonViTinhsReq, Long> {
  @Query("SELECT c FROM DonViTinhs c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId})"
          + " AND (:#{#param.tenDonViTinh} IS NULL OR lower(c.tenDonViTinh) LIKE lower(concat('%',CONCAT(:#{#param.tenDonViTinh},'%'))))"
          + " AND (:#{#param.maNhaThuoc} IS NULL OR lower(c.maNhaThuoc) LIKE lower(concat('%',CONCAT(:#{#param.maNhaThuoc},'%'))))"
          + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
          + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
          + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
          + " ORDER BY c.id desc"
  )
  Page<DonViTinhs> searchPage(@Param("param") DonViTinhsReq param, Pageable pageable);
  
  
  @Query("SELECT c FROM DonViTinhs c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId})"
          + " AND (:#{#param.tenDonViTinh} IS NULL OR lower(c.tenDonViTinh) LIKE lower(concat('%',CONCAT(:#{#param.tenDonViTinh},'%'))))"
          + " AND (:#{#param.maNhaThuoc} IS NULL OR lower(c.maNhaThuoc) LIKE lower(concat('%',CONCAT(:#{#param.maNhaThuoc},'%'))))"
          + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
          + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
          + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
          + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
          + " ORDER BY c.id desc"
  )
  List<DonViTinhs> searchList(@Param("param") DonViTinhsReq param);

}
