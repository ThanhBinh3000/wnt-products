package vn.com.gsoft.products.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.gsoft.products.entity.PhieuKiemKeChiTiets;
import vn.com.gsoft.products.model.dto.PhieuKiemKeChiTietsReq;

import java.util.List;

@Repository
public interface PhieuKiemKeChiTietsRepository extends BaseRepository<PhieuKiemKeChiTiets, PhieuKiemKeChiTietsReq, Long> {
  @Query("SELECT c FROM PhieuKiemKeChiTiets c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.thuocThuocId} IS NULL OR c.thuocThuocId = :#{#param.thuocThuocId}) "
          + " AND (:#{#param.tonKho} IS NULL OR c.tonKho = :#{#param.tonKho}) "
          + " AND (:#{#param.thucTe} IS NULL OR c.thucTe = :#{#param.thucTe}) "
          + " AND (:#{#param.phieuKiemKeMaPhieuKiemKe} IS NULL OR c.phieuKiemKeMaPhieuKiemKe = :#{#param.phieuKiemKeMaPhieuKiemKe}) "
          + " AND (:#{#param.donGia} IS NULL OR c.donGia = :#{#param.donGia}) "
          + " AND (:#{#param.soLo} IS NULL OR lower(c.soLo) LIKE lower(concat('%',CONCAT(:#{#param.soLo},'%'))))"
          + " AND (:#{#param.hanDung} IS NULL OR c.hanDung >= :#{#param.hanDungFrom}) "
          + " AND (:#{#param.hanDung} IS NULL OR c.hanDung <= :#{#param.hanDungTo}) "
          + " AND (:#{#param.archiveDrugId} IS NULL OR c.archiveDrugId = :#{#param.archiveDrugId}) "
          + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
          + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
          + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
          + " ORDER BY c.id desc"
  )
  Page<PhieuKiemKeChiTiets> searchPage(@Param("param") PhieuKiemKeChiTietsReq param, Pageable pageable);
  
  
  @Query("SELECT c FROM PhieuKiemKeChiTiets c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.thuocThuocId} IS NULL OR c.thuocThuocId = :#{#param.thuocThuocId}) "
          + " AND (:#{#param.tonKho} IS NULL OR c.tonKho = :#{#param.tonKho}) "
          + " AND (:#{#param.thucTe} IS NULL OR c.thucTe = :#{#param.thucTe}) "
          + " AND (:#{#param.phieuKiemKeMaPhieuKiemKe} IS NULL OR c.phieuKiemKeMaPhieuKiemKe = :#{#param.phieuKiemKeMaPhieuKiemKe}) "
          + " AND (:#{#param.donGia} IS NULL OR c.donGia = :#{#param.donGia}) "
          + " AND (:#{#param.soLo} IS NULL OR lower(c.soLo) LIKE lower(concat('%',CONCAT(:#{#param.soLo},'%'))))"
          + " AND (:#{#param.hanDung} IS NULL OR c.hanDung >= :#{#param.hanDungFrom}) "
          + " AND (:#{#param.hanDung} IS NULL OR c.hanDung <= :#{#param.hanDungTo}) "
          + " AND (:#{#param.archiveDrugId} IS NULL OR c.archiveDrugId = :#{#param.archiveDrugId}) "
          + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
          + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
          + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
          + " ORDER BY c.id desc"
  )
  List<PhieuKiemKeChiTiets> searchList(@Param("param") PhieuKiemKeChiTietsReq param);

}
