package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.PhieuNhaps;
import vn.com.gsoft.products.model.dto.PhieuNhapsReq;
import vn.com.gsoft.products.repository.PhieuNhapsRepository;
import vn.com.gsoft.products.service.KafkaProducer;
import vn.com.gsoft.products.service.PhieuNhapsService;


@Service
@Log4j2
public class PhieuNhapsServiceImpl extends BaseServiceImpl<PhieuNhaps, PhieuNhapsReq, Long> implements PhieuNhapsService {

    private PhieuNhapsRepository hdrRepo;
    private KafkaProducer kafkaProducer;
    @Value("${wnt.kafka.internal.consumer.topic.inventory}")
    private String topicName;

    @Autowired
    public PhieuNhapsServiceImpl(PhieuNhapsRepository hdrRepo,
                                 KafkaProducer kafkaProducer) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public PhieuNhaps createByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        //todo
        return null;
    }

    @Override
    public PhieuNhaps updateByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        //todo
        return null;
    }
}
