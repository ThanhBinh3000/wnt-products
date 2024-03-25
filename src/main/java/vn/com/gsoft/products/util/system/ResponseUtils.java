package vn.com.gsoft.products.util.system;

import lombok.extern.slf4j.Slf4j;
import vn.com.gsoft.products.enums.EnumResponse;
import vn.com.gsoft.products.response.BaseResponse;

@Slf4j
public class ResponseUtils {
    public static BaseResponse ok(Object data) {
        BaseResponse resp = new BaseResponse();
        resp.setData(data);
        resp.setStatusCode(EnumResponse.RESP_SUCC.getValue());
        resp.setMessage(EnumResponse.RESP_SUCC.getDescription());
        return resp;
    }
}
