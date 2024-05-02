package vn.com.gsoft.products.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.repository.feign.FileFeign;


@Service
@Log4j2
public class FileServiceImpl {


    @Autowired
    private FileFeign fileFeign;

    public FileDto saveFile(FileDto fileReq){
        ObjectMapper objectMapper = new ObjectMapper();
        String baseResponse = this.fileFeign.uploadFile(fileReq, fileReq.getFile());
        FileDto fileDto = objectMapper.convertValue(baseResponse, FileDto.class);
        return fileDto;
    }

}
