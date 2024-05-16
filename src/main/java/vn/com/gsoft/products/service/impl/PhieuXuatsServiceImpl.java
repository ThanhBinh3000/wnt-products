package vn.com.gsoft.products.service.impl;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.ENoteType;
import vn.com.gsoft.products.constant.InventoryConstant;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.PhieuXuatsReq;
import vn.com.gsoft.products.model.system.ApplicationSetting;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.model.system.WrapData;
import vn.com.gsoft.products.repository.KhachHangsRepository;
import vn.com.gsoft.products.repository.NhaCungCapsRepository;
import vn.com.gsoft.products.repository.PhieuXuatChiTietsRepository;
import vn.com.gsoft.products.repository.PhieuXuatsRepository;
import vn.com.gsoft.products.service.KafkaProducer;
import vn.com.gsoft.products.service.PhieuXuatsService;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


@Service
@Log4j2
public class PhieuXuatsServiceImpl extends BaseServiceImpl<PhieuXuats, PhieuXuatsReq, Long> implements PhieuXuatsService {
    private PhieuXuatsRepository hdrRepo;
    private PhieuXuatChiTietsRepository phieuXuatChiTietsRepository;
    private NhaCungCapsRepository nhaCungCapsRepository;
    private KhachHangsRepository khachHangsRepository;
    private KafkaProducer kafkaProducer;
    @Value("${wnt.kafka.internal.consumer.topic.inventory}")
    private String topicName;

    @Autowired
    public PhieuXuatsServiceImpl(PhieuXuatsRepository hdrRepo,
                                 PhieuXuatChiTietsRepository phieuXuatChiTietsRepository,
                                 NhaCungCapsRepository nhaCungCapsRepository,
                                 KhachHangsRepository khachHangsRepository,
                                 KafkaProducer kafkaProducer) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.kafkaProducer = kafkaProducer;
        this.phieuXuatChiTietsRepository = phieuXuatChiTietsRepository;
        this.nhaCungCapsRepository = nhaCungCapsRepository;
        this.khachHangsRepository = khachHangsRepository;
    }

    @Override
    public PhieuXuats init(Long maLoaiXuatNhap, Long id) throws Exception {
        Profile currUser = getLoggedUser();
        String storeCode = currUser.getNhaThuoc().getMaNhaThuoc();
        List<ApplicationSetting> applicationSetting = currUser.getApplicationSettings();
        PhieuXuats data = null;
        if (id == null) {
            data = new PhieuXuats();
            Long soPhieuXuat = hdrRepo.findBySoPhieuXuatMax(storeCode, maLoaiXuatNhap);
            if (soPhieuXuat == null) {
                soPhieuXuat = 0L;
            }
            data.setSoPhieuXuat(soPhieuXuat + 1);
            data.setUId(UUID.randomUUID());
            data.setNgayXuat(new Date());

            if (Objects.equals(maLoaiXuatNhap, ENoteType.ReturnToSupplier)) {
                // tìm nhà cung cấp nhập lẻ
                Optional<NhaCungCaps> ncc = this.nhaCungCapsRepository.findKhachHangLe(storeCode);
                if (ncc.isPresent()) {
                    data.setNhaCungCapMaNhaCungCap(ncc.get().getId());
                } else {
                    throw new Exception("Không tìm thấy khách hàng lẻ!");
                }
            } else if (Objects.equals(maLoaiXuatNhap, ENoteType.Delivery)) {
                // tìm khách hàng lẻ
                Optional<KhachHangs> kh = this.khachHangsRepository.findKhachHangLe(storeCode);
                if (kh.isPresent()) {
                    data.setKhachHangMaKhachHang(kh.get().getId());
                } else {
                    throw new Exception("Không tìm thấy khách hàng lẻ!");
                }
            }

        } else {
            Optional<PhieuXuats> phieuXuats = hdrRepo.findById(id);
            if (phieuXuats.isPresent()) {
                data = phieuXuats.get();
                data.setId(null);
                Long soPhieuXuat = hdrRepo.findBySoPhieuXuatMax(storeCode, maLoaiXuatNhap);
                if (soPhieuXuat == null) {
                    soPhieuXuat = 0L;
                }
                data.setUId(UUID.randomUUID());
                data.setSoPhieuXuat(soPhieuXuat + 1);
                data.setNgayXuat(new Date());
                data.setCreatedByUserId(null);
                data.setModifiedByUserId(null);
                data.setCreated(null);
                data.setModified(null);
            } else {
                throw new Exception("Không tìm thấy phiếu copy!");
            }
        }
        return data;
    }

    @Override
    public PhieuXuats detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<PhieuXuats> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
            List<PhieuXuatChiTiets> phieuXuatMaPhieuXuat = phieuXuatChiTietsRepository.findByPhieuXuatMaPhieuXuatAndRecordStatusId(optional.get().getId(), RecordStatusContains.ACTIVE);
            phieuXuatMaPhieuXuat = phieuXuatMaPhieuXuat.stream().filter(item -> RecordStatusContains.ACTIVE == item.getRecordStatusId()).collect(Collectors.toList());
            optional.get().setChiTiets(phieuXuatMaPhieuXuat);
        }
        return optional.get();
    }

    @Override
    public PhieuXuats createByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        PhieuXuats px = new PhieuXuats();
        BeanUtils.copyProperties(e, px, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
        PhieuXuats init = init(ENoteType.InventoryAdjustment.longValue(), null);
        px.setSoPhieuXuat(init.getSoPhieuXuat());
        px.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        px.setStoreId(userInfo.getNhaThuoc().getId());
        px.setTargetId(null);
        px.setTargetStoreId(null);
        px.setTargetManagementId(null);
        px.setRecordStatusId(RecordStatusContains.ACTIVE);
        px.setIsModified(false);
        e.setCreated(new Date());
        e.setCreatedByUserId(getLoggedUser().getId());
        px = hdrRepo.save(px);
        // save chi tiết
        px.setChiTiets(new ArrayList<>());
        for (PhieuKiemKeChiTiets chiTiet : e.getChiTiets()) {
            PhieuXuatChiTiets ct = new PhieuXuatChiTiets();
            BeanUtils.copyProperties(chiTiet, ct, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
            ct.setPhieuXuatMaPhieuXuat(px.getId());
            ct.setSoLuong(Math.abs(ct.getSoLuong().doubleValue()));
            px.getChiTiets().add(ct);
        }
        this.phieuXuatChiTietsRepository.saveAll(px.getChiTiets());
        updateInventory(px);
        return px;
    }

    @Override
    public PhieuXuats updateByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        PhieuXuats phieuXuats = detail(e.getPhieuXuatMaPhieuXuat());
        if(phieuXuats ==null){
            throw new Exception("Không tìm thấy phiếu xuất!");
        }
        delete(phieuXuats.getId());
        PhieuXuats px = new PhieuXuats();
        BeanUtils.copyProperties(e, px, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
        PhieuXuats init = init(ENoteType.InventoryAdjustment.longValue(), null);
        px.setSoPhieuXuat(init.getSoPhieuXuat());
        px.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        px.setStoreId(userInfo.getNhaThuoc().getId());
        px.setTargetId(null);
        px.setTargetStoreId(null);
        px.setTargetManagementId(null);
        px.setRecordStatusId(RecordStatusContains.ACTIVE);
        px.setIsModified(false);
        e.setCreated(phieuXuats.getCreated());
        e.setCreatedByUserId(phieuXuats.getCreatedByUserId());
        e.setModified(new Date());
        e.setModifiedByUserId(userInfo.getId());
        px = hdrRepo.save(px);
        // save chi tiết
        px.setChiTiets(new ArrayList<>());
        for (PhieuKiemKeChiTiets chiTiet : e.getChiTiets()) {
            PhieuXuatChiTiets ct = new PhieuXuatChiTiets();
            BeanUtils.copyProperties(chiTiet, ct, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
            ct.setPhieuXuatMaPhieuXuat(px.getId());
            ct.setSoLuong(Math.abs(ct.getSoLuong().doubleValue()));
            px.getChiTiets().add(ct);
        }
        this.phieuXuatChiTietsRepository.saveAll(px.getChiTiets());
        updateInventory(px);
        return px;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        PhieuXuats phieuXuats = detail(id);
        phieuXuats.setRecordStatusId(RecordStatusContains.DELETED);
        hdrRepo.save(phieuXuats);
        updateInventory(phieuXuats);
        return true;
    }

    private void updateInventory(PhieuXuats e) throws InterruptedException, ExecutionException, TimeoutException {
        Gson gson = new Gson();
        for (PhieuXuatChiTiets chiTiet : e.getChiTiets()) {
            String key = e.getNhaThuocMaNhaThuoc() + "-" + chiTiet.getThuocThuocId();
            WrapData data = new WrapData();
            PhieuXuats px = new PhieuXuats();
            BeanUtils.copyProperties(e, px);
            px.setChiTiets(List.copyOf(Collections.singleton(chiTiet)));
            data.setCode(InventoryConstant.XUAT);
            data.setSendDate(new Date());
            data.setData(px);
            this.kafkaProducer.sendInternal(topicName, key, gson.toJson(data));
        }
    }
}
