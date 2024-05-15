package vn.com.gsoft.products.service.impl;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.InventoryConstant;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.PhieuNhapChiTiets;
import vn.com.gsoft.products.entity.PhieuNhaps;
import vn.com.gsoft.products.model.dto.PhieuNhapsReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.model.system.WrapData;
import vn.com.gsoft.products.repository.PhieuNhapChiTietsRepository;
import vn.com.gsoft.products.repository.PhieuNhapsRepository;
import vn.com.gsoft.products.service.KafkaProducer;
import vn.com.gsoft.products.service.PhieuNhapsService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


@Service
@Log4j2
public class PhieuNhapsServiceImpl extends BaseServiceImpl<PhieuNhaps, PhieuNhapsReq, Long> implements PhieuNhapsService {

    private PhieuNhapsRepository hdrRepo;
    private PhieuNhapChiTietsRepository phieuNhapChiTietsRepository;
    private KafkaProducer kafkaProducer;
    @Value("${wnt.kafka.internal.consumer.topic.inventory}")
    private String topicName;

    @Autowired
    public PhieuNhapsServiceImpl(PhieuNhapsRepository hdrRepo,
                                 PhieuNhapChiTietsRepository phieuNhapChiTietsRepository,
                                 KafkaProducer kafkaProducer) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.phieuNhapChiTietsRepository = phieuNhapChiTietsRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public PhieuNhaps detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<PhieuNhaps> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        PhieuNhaps phieuNhaps = optional.get();
        List<PhieuNhapChiTiets> chiTiets = phieuNhapChiTietsRepository.findAllByPhieuNhapMaPhieuNhap(phieuNhaps.getId());
        chiTiets = chiTiets.stream().filter(item -> RecordStatusContains.ACTIVE == item.getRecordStatusId()).collect(Collectors.toList());
        phieuNhaps.setChiTiets(chiTiets);
        return phieuNhaps;
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

    private void updateInventory(PhieuNhaps e) throws ExecutionException, InterruptedException, TimeoutException {
        Gson gson = new Gson();
        for (PhieuNhapChiTiets chiTiet : e.getChiTiets()) {
            String key = e.getNhaThuocMaNhaThuoc() + "-" + chiTiet.getThuocThuocId();
            WrapData data = new WrapData();
            PhieuNhaps px = new PhieuNhaps();
            BeanUtils.copyProperties(e, px);
            px.setChiTiets(List.copyOf(Collections.singleton(chiTiet)));
            data.setCode(InventoryConstant.NHAP);
            data.setSendDate(new Date());
            data.setData(px);
            this.kafkaProducer.sendInternal(topicName, key, gson.toJson(data));
        }
    }

}
