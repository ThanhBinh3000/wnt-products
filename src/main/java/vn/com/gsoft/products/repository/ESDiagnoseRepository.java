package vn.com.gsoft.products.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.ESDiagnose;

import java.util.List;

@Repository
public interface ESDiagnoseRepository extends CrudRepository<ESDiagnose, Long> {

    List<ESDiagnose> findByIdIn(List<Long> ids);
}
