package vn.com.gsoft.products.service;


import vn.com.gsoft.products.entity.PhieuDuTru;
import vn.com.gsoft.products.entity.ReportTemplateResponse;
import vn.com.gsoft.products.model.dto.PhieuDuTruReq;

import java.util.HashMap;

public interface PhieuDuTruService extends BaseService<PhieuDuTru, PhieuDuTruReq, Long> {

    ReportTemplateResponse preview(HashMap<String, Object> hashMap) throws Exception;
}