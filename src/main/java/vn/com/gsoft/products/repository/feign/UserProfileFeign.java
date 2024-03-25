package vn.com.gsoft.products.repository.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import vn.com.gsoft.products.response.BaseResponse;

@FeignClient(name = "wnt-security")
public interface UserProfileFeign {
    @GetMapping("/profile")
    BaseResponse getProfile();
}
