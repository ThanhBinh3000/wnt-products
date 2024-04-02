package vn.com.gsoft.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;

import java.util.List;

@Repository
public interface PhieuKiemKesRepository extends BaseRepository<PhieuKiemKes, PhieuKiemKesReq, Long> {
    @Query("SELECT c FROM PhieuKiemKes c " +
            "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.phieuNhapMaPhieuNhap} IS NULL OR c.phieuNhapMaPhieuNhap = :#{#param.phieuNhapMaPhieuNhap}) "
            + " AND (:#{#param.phieuXuatMaPhieuXuat} IS NULL OR c.phieuXuatMaPhieuXuat = :#{#param.phieuXuatMaPhieuXuat}) "
            + " AND (:#{#param.nhaThuocMaNhaThuoc} IS NULL OR lower(c.nhaThuocMaNhaThuoc) LIKE lower(concat('%',CONCAT(:#{#param.nhaThuocMaNhaThuoc},'%'))))"
            + " AND (:#{#param.userProfileUserId} IS NULL OR c.userProfileUserId = :#{#param.userProfileUserId}) "
            + " AND (:#{#param.soPhieu} IS NULL OR c.soPhieu = :#{#param.soPhieu}) "
            + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
            + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
            + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
            + " ORDER BY c.id desc"
    )
    Page<PhieuKiemKes> searchPage(@Param("param") PhieuKiemKesReq param, Pageable pageable);


    @Query("SELECT c FROM PhieuKiemKes c " +
            "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.phieuNhapMaPhieuNhap} IS NULL OR c.phieuNhapMaPhieuNhap = :#{#param.phieuNhapMaPhieuNhap}) "
            + " AND (:#{#param.phieuXuatMaPhieuXuat} IS NULL OR c.phieuXuatMaPhieuXuat = :#{#param.phieuXuatMaPhieuXuat}) "
            + " AND (:#{#param.nhaThuocMaNhaThuoc} IS NULL OR lower(c.nhaThuocMaNhaThuoc) LIKE lower(concat('%',CONCAT(:#{#param.nhaThuocMaNhaThuoc},'%'))))"
            + " AND (:#{#param.userProfileUserId} IS NULL OR c.userProfileUserId = :#{#param.userProfileUserId}) "
            + " AND (:#{#param.soPhieu} IS NULL OR c.soPhieu = :#{#param.soPhieu}) "
            + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
            + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
            + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId}) "
            + " ORDER BY c.id desc"
    )
    List<PhieuKiemKes> searchList(@Param("param") PhieuKiemKesReq param);

}
