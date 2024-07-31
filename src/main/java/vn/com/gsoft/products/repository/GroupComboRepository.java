package vn.com.gsoft.products.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.GroupCombo;
import vn.com.gsoft.products.entity.ProcessDtl;

import java.util.List;

@Repository
public interface GroupComboRepository extends CrudRepository<GroupCombo, Long> {

    List<GroupCombo> findAllByDrugId(Long drugId);
}
