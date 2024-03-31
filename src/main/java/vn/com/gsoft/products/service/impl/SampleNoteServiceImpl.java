package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.SampleNote;
import vn.com.gsoft.products.model.dto.SampleNoteReq;
import vn.com.gsoft.products.repository.SampleNoteRepository;
import vn.com.gsoft.products.service.SampleNoteService;


@Service
@Log4j2
public class SampleNoteServiceImpl extends BaseServiceImpl<SampleNote, SampleNoteReq, Long> implements SampleNoteService {

    private SampleNoteRepository hdrRepo;

    @Autowired
    public SampleNoteServiceImpl(SampleNoteRepository hdrRepo) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
    }

}
