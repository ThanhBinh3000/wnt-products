package vn.com.gsoft.products.service;

import vn.com.gsoft.products.model.system.Profile;

import java.util.Optional;

public interface UserService extends BaseService {
    Optional<Profile> findUserByToken(String token);

    Optional<Profile> findUserByUsername(String token);

}
