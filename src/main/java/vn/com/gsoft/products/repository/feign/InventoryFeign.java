package vn.com.gsoft.products.repository.feign;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.system.BaseResponse;

@FeignClient(name = "wnt-inventory")
public interface InventoryFeign {

    @PostMapping("/inventory/search-detail")
    @Headers({ "Accept: application/json; charset=utf-8", "Content-Type: application/x-www-form-urlencoded" })
    BaseResponse getDetailInvetory(InventoryReq req);
}