package vn.com.gsoft.products.service;

import jakarta.servlet.http.HttpServletResponse;
import vn.com.gsoft.products.entity.PhieuDuTru;
import vn.com.gsoft.products.entity.ReportTemplateResponse;
import vn.com.gsoft.products.model.dto.PhieuDuTruReq;

import java.util.List;

public interface PhieuDuTruService extends BaseService<PhieuDuTru, PhieuDuTruReq, Long> {

    ReportTemplateResponse preview(PhieuDuTruReq object) throws Exception;

    List<PhieuDuTru> createNhaCC(List<PhieuDuTruReq> req) throws Exception;

    void export(PhieuDuTruReq req, HttpServletResponse response) throws Exception;
}