package vn.com.gsoft.products.service;

import vn.com.gsoft.products.model.system.Profile;

public interface BaseService {
    Profile getLoggedUser() throws Exception;

}
