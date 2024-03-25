package vn.com.gsoft.products.service;

import vn.com.gsoft.products.model.system.Profile;

import java.util.Optional;

public interface UserService{
    Optional<Profile> findUserByToken(String token);

}
