package vn.com.gsoft.products.model.system;

import lombok.Data;

@Data
public class BaseResponse {
	Object data;
	Object otherData;
	int status;//0: succ <>0: fail
	String message;
	Object included;
}