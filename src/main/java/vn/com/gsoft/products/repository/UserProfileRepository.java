package vn.com.gsoft.products.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.UserProfile;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {

}
