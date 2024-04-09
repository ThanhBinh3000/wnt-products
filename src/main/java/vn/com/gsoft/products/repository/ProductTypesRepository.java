package vn.com.gsoft.products.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.gsoft.products.entity.ProductTypes;
import vn.com.gsoft.products.model.dto.ProductTypesReq;

import java.util.List;

@Repository
public interface ProductTypesRepository extends BaseRepository<ProductTypes, ProductTypesReq, Long> {
  @Query("SELECT c FROM ProductTypes c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.name} IS NULL OR lower(c.name) LIKE lower(concat('%',CONCAT(:#{#param.name},'%'))))"
          + " AND (:#{#param.displayName} IS NULL OR lower(c.displayName) LIKE lower(concat('%',CONCAT(:#{#param.displayName},'%'))))"
          + " ORDER BY c.id desc"
  )
  Page<ProductTypes> searchPage(@Param("param") ProductTypesReq param, Pageable pageable);
  
  
  @Query("SELECT c FROM ProductTypes c " +
         "WHERE 1=1 "
          + " AND (:#{#param.id} IS NULL OR c.id = :#{#param.id}) "
          + " AND (:#{#param.name} IS NULL OR lower(c.name) LIKE lower(concat('%',CONCAT(:#{#param.name},'%'))))"
          + " AND (:#{#param.displayName} IS NULL OR lower(c.displayName) LIKE lower(concat('%',CONCAT(:#{#param.displayName},'%'))))"
          + " ORDER BY c.id desc"
  )
  List<ProductTypes> searchList(@Param("param") ProductTypesReq param);

}
