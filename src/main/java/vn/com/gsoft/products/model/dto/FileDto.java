package vn.com.gsoft.products.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FileDto implements Serializable {

    private static final long serialVersionUID = 232836038145089522L;

    private String title;

    private String description;
    private String folder;
    private MultipartFile file;

    private String url;

    private Long size;

    private String filename;
    
    private String dataType;
    private Long dataId;

    public FileDto(String title, String description, String folder, String url, Long size, String filename,String dataType,Long dataId) {
        this.title = title;
        this.description = description;
        this.folder = folder;
        this.url = url;
        this.size = size;
        this.filename = filename;
        this.dataType = dataType;
        this.dataId = dataId;
    }

    public FileDto(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileDto fileRes = objectMapper.readValue(jsonString, FileDto.class);
            this.title = fileRes.title;
            this.description = fileRes.description;
            this.url = fileRes.url;
            this.size = fileRes.size;
            this.filename = fileRes.filename;
            this.dataType = fileRes.dataType;
            this.dataId = fileRes.dataId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}