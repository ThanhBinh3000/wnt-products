package vn.com.gsoft.products.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.ProcessDtl;

@Repository
public interface ProcessDtlRepository extends CrudRepository<ProcessDtl, Long> {

}
