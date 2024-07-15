package vn.com.gsoft.products.model.dto;

import jakarta.persistence.*;
import lombok.*;
import vn.com.gsoft.products.entity.BaseEntity;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceGoodsAndBundleGoodsReq extends BaseEntity {
    private Long id;
    private Long drugId;
    private Long drugIdMap;
    private Long typeId;
    private String drugStoreCode;
}

