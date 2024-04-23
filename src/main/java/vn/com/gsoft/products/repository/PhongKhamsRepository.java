package vn.com.gsoft.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.DonViTinhs;
import vn.com.gsoft.products.entity.PhongKhams;
import vn.com.gsoft.products.model.dto.DonViTinhsReq;

import java.util.List;

@Repository
public interface PhongKhamsRepository extends CrudRepository<PhongKhams, Long> {

}
