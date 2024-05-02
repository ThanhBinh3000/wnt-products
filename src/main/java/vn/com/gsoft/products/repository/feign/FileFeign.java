package vn.com.gsoft.products.repository.feign;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.system.BaseResponse;

@FeignClient(name = "wnt-file-dev")
public interface FileFeign {

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("request") FileDto request, @RequestPart("file") MultipartFile file);
}
