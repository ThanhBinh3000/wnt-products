package vn.com.gsoft.products.service;


import org.springframework.data.domain.Page;
import vn.com.gsoft.products.entity.ReportTemplateResponse;
import vn.com.gsoft.products.entity.SampleNote;
import vn.com.gsoft.products.model.dto.SampleNoteReq;

import java.util.HashMap;

public interface SampleNoteService extends BaseService<SampleNote, SampleNoteReq, Long> {
    ReportTemplateResponse preview(HashMap<String, Object> hashMap) throws Exception;

    //Lấy lịch sử giao dịch đơn thuốc
    Page<SampleNote> getTranSampleNotes(SampleNoteReq req) throws Exception;
}