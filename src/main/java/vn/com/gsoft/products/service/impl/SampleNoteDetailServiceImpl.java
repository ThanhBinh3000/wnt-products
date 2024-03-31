package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.SampleNoteDetail;
import vn.com.gsoft.products.model.dto.SampleNoteDetailReq;
import vn.com.gsoft.products.repository.SampleNoteDetailRepository;
import vn.com.gsoft.products.service.SampleNoteDetailService;


@Service
@Log4j2
public class SampleNoteDetailServiceImpl extends BaseServiceImpl<SampleNoteDetail, SampleNoteDetailReq, Long> implements SampleNoteDetailService {

    private SampleNoteDetailRepository hdrRepo;

    @Autowired
    public SampleNoteDetailServiceImpl(SampleNoteDetailRepository hdrRepo) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
    }

}
