package vn.com.gsoft.products.model.dto;

import lombok.Data;

@Data
public class ReportImage {
    String nameImage;
    String pathImage;

    public ReportImage(String nameImage, String pathImage) {
        this.nameImage = nameImage;
        this.pathImage = pathImage;
    }
}
