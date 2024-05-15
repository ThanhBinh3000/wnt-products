package vn.com.gsoft.products.model.system;
import lombok.Data;

import java.util.Date;

@Data
public class WrapData {
    private String code;
    private Date sendDate;
    private Object data;
}
