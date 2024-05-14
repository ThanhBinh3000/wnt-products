package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.PhieuXuats;
import vn.com.gsoft.products.model.dto.PhieuXuatsReq;
import vn.com.gsoft.products.repository.PhieuXuatsRepository;
import vn.com.gsoft.products.service.KafkaProducer;
import vn.com.gsoft.products.service.PhieuXuatsService;


@Service
@Log4j2
public class PhieuXuatsServiceImpl extends BaseServiceImpl<PhieuXuats, PhieuXuatsReq, Long> implements PhieuXuatsService {
    private PhieuXuatsRepository hdrRepo;

    private KafkaProducer kafkaProducer;
    @Value("${wnt.kafka.internal.consumer.topic.inventory}")
    private String topicName;

    @Autowired
    public PhieuXuatsServiceImpl(PhieuXuatsRepository hdrRepo,
                                 KafkaProducer kafkaProducer) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.kafkaProducer = kafkaProducer;
    }


    @Override
    public PhieuXuats createByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        //todo
        return null;
    }

    @Override
    public PhieuXuats updateByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        //todo
        return null;
    }
}
