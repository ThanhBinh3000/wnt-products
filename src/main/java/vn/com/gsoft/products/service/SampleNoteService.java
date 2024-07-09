package vn.com.gsoft.products.service;


import vn.com.gsoft.products.entity.ReportTemplateResponse;
import vn.com.gsoft.products.entity.SampleNote;
import vn.com.gsoft.products.model.dto.SampleNoteReq;

import java.util.HashMap;

public interface SampleNoteService extends BaseService<SampleNote, SampleNoteReq, Long> {
    ReportTemplateResponse preview(HashMap<String, Object> hashMap) throws Exception;

}