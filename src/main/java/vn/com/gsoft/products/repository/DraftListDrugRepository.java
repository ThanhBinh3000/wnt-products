package vn.com.gsoft.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.DraftListDrug;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.model.dto.DraftListDrugReq;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;

import java.util.List;

@Repository
public interface DraftListDrugRepository extends CrudRepository<DraftListDrug, Long> {
    List<DraftListDrug> findByDrugId(Long drugId);
}
