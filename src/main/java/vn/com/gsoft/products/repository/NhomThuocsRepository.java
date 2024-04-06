package vn.com.gsoft.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;

import java.util.List;

@Repository
public interface NhomThuocsRepository extends BaseRepository<NhomThuocs, NhomThuocsReq, Long> {
    @Query("SELECT c FROM NhomThuocs c " +
            "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId})"
            + " AND (:#{#param.tenNhomThuoc} IS NULL OR lower(c.tenNhomThuoc) LIKE lower(concat('%',CONCAT(:#{#param.tenNhomThuoc},'%'))))"
            + " AND (:#{#param.kyHieuNhomThuoc} IS NULL OR lower(c.kyHieuNhomThuoc) LIKE lower(concat('%',CONCAT(:#{#param.kyHieuNhomThuoc},'%'))))"
            + " AND (:#{#param.maNhaThuoc} IS NULL OR lower(c.maNhaThuoc) LIKE lower(concat('%',CONCAT(:#{#param.maNhaThuoc},'%'))))"
            + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
            + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
            + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
            + " AND (:#{#param.typeGroupProduct} IS NULL OR c.typeGroupProduct = :#{#param.typeGroupProduct}) "
            + " ORDER BY c.id desc"
    )
    Page<NhomThuocs> searchPage(@Param("param") NhomThuocsReq param, Pageable pageable);


    @Query("SELECT c FROM NhomThuocs c " +
            "WHERE 1=1 "
            + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
            + " AND (:#{#param.recordStatusId} IS NULL OR c.recordStatusId = :#{#param.recordStatusId})"
            + " AND (:#{#param.tenNhomThuoc} IS NULL OR lower(c.tenNhomThuoc) LIKE lower(concat('%',CONCAT(:#{#param.tenNhomThuoc},'%'))))"
            + " AND (:#{#param.kyHieuNhomThuoc} IS NULL OR lower(c.kyHieuNhomThuoc) LIKE lower(concat('%',CONCAT(:#{#param.kyHieuNhomThuoc},'%'))))"
            + " AND (:#{#param.maNhaThuoc} IS NULL OR lower(c.maNhaThuoc) LIKE lower(concat('%',CONCAT(:#{#param.maNhaThuoc},'%'))))"
            + " AND (:#{#param.referenceId} IS NULL OR c.referenceId = :#{#param.referenceId}) "
            + " AND (:#{#param.archivedId} IS NULL OR c.archivedId = :#{#param.archivedId}) "
            + " AND (:#{#param.storeId} IS NULL OR c.storeId = :#{#param.storeId}) "
            + " AND (:#{#param.typeGroupProduct} IS NULL OR c.typeGroupProduct = :#{#param.typeGroupProduct}) "
            + " ORDER BY c.id desc"
    )
    List<NhomThuocs> searchList(@Param("param") NhomThuocsReq param);

}
