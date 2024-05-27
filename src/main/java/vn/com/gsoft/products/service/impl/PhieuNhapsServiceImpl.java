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
import vn.com.gsoft.products.model.dto.PhieuNhapsReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.model.system.WrapData;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.service.KafkaProducer;
import vn.com.gsoft.products.service.PhieuNhapsService;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


@Service
@Log4j2
public class PhieuNhapsServiceImpl extends BaseServiceImpl<PhieuNhaps, PhieuNhapsReq, Long> implements PhieuNhapsService {

    private PhieuNhapsRepository hdrRepo;
    private PhieuNhapChiTietsRepository phieuNhapChiTietsRepository;
    private NhaCungCapsRepository nhaCungCapsRepository;
    private KhachHangsRepository khachHangsRepository;
    private NhaThuocsRepository nhaThuocsRepository;
    private KafkaProducer kafkaProducer;
    @Value("${wnt.kafka.internal.consumer.topic.inventory}")
    private String topicName;

    @Autowired
    public PhieuNhapsServiceImpl(PhieuNhapsRepository hdrRepo,
                                 PhieuNhapChiTietsRepository phieuNhapChiTietsRepository,
                                 NhaCungCapsRepository nhaCungCapsRepository,
                                 KhachHangsRepository khachHangsRepository,
                                 NhaThuocsRepository nhaThuocsRepository,
                                 KafkaProducer kafkaProducer) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.phieuNhapChiTietsRepository = phieuNhapChiTietsRepository;
        this.kafkaProducer = kafkaProducer;
        this.nhaCungCapsRepository = nhaCungCapsRepository;
        this.khachHangsRepository = khachHangsRepository;
        this.nhaThuocsRepository = nhaThuocsRepository;

    }

    @Override
    public PhieuNhaps init(Long maLoaiXuatNhap, Long id) throws Exception {
        Profile currUser = getLoggedUser();
        String maNhaThuoc = currUser.getNhaThuoc().getMaNhaThuoc();
        PhieuNhaps data = null;
        if (id == null) {
            data = new PhieuNhaps();
            Long soPhieuNhap = hdrRepo.findBySoPhieuNhapMax(maNhaThuoc, maLoaiXuatNhap);
            if (soPhieuNhap == null) {
                soPhieuNhap = 1L;
            } else {
                soPhieuNhap += 1;
            }
            data.setSoPhieuNhap(soPhieuNhap);
            data.setUId(UUID.randomUUID());
            data.setNgayNhap(new Date());

//            if (Objects.equals(maLoaiXuatNhap, ENoteType.Receipt)) {
//                // tìm nhà cung cấp nhập lẻ
//                Optional<NhaCungCaps> ncc = this.nhaCungCapsRepository.findKhachHangLe(storeCode);
//                if (ncc.isPresent()) {
//                    data.setNhaCungCapMaNhaCungCap(ncc.get().getId());
//                } else {
//                    throw new Exception("Không tìm thấy khách hàng lẻ!");
//                }
//            } else if (Objects.equals(maLoaiXuatNhap, ENoteType.Delivery)) {
//                // tìm khách hàng lẻ
//                Optional<KhachHangs> kh = this.khachHangsRepository.findKhachHangLe(storeCode);
//                if (kh.isPresent()) {
//                    data.setKhachHangMaKhachHang(kh.get().getId());
//                } else {
//                    throw new Exception("Không tìm thấy khách hàng lẻ!");
//                }
//            }
        } else {
            Optional<PhieuNhaps> phieuNhaps = hdrRepo.findById(id);
            if (phieuNhaps.isPresent()) {
                data = phieuNhaps.get();
                data.setId(null);
                Long soPhieuNhap = hdrRepo.findBySoPhieuNhapMax(maNhaThuoc, maLoaiXuatNhap);
                if (soPhieuNhap == null) {
                    soPhieuNhap = 1L;
                }
                data.setUId(UUID.randomUUID());
                data.setSoPhieuNhap(soPhieuNhap);
                data.setNgayNhap(new Date());
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
        chiTiets = chiTiets.stream().filter(item -> item.getRecordStatusId() != null && RecordStatusContains.ACTIVE == item.getRecordStatusId()).collect(Collectors.toList());
        phieuNhaps.setChiTiets(chiTiets);
        return phieuNhaps;
    }

    @Override
    public PhieuNhaps createByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        PhieuNhaps pn = new PhieuNhaps();
        BeanUtils.copyProperties(e, pn, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
        PhieuNhaps init = init(ENoteType.InventoryAdjustment.longValue(), null);
        pn.setSoPhieuNhap(init.getSoPhieuNhap());
        pn.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        pn.setStoreId(userInfo.getNhaThuoc().getId());
        pn.setTargetId(null);
        pn.setTargetStoreId(null);
        pn.setTargetManagementId(null);
        pn.setRecordStatusId(RecordStatusContains.ACTIVE);
        pn.setIsModified(false);
        pn.setCreated(new Date());
        pn.setCreatedByUserId(getLoggedUser().getId());
        pn.setDaTra(0d);
        pn.setTongTien(0d);
        pn.setVat(0);
        pn = hdrRepo.save(pn);
        // save chi tiết
        pn.setChiTiets(new ArrayList<>());
        Double tongTien = 0d;
        for (PhieuKiemKeChiTiets chiTiet : e.getChiTiets()) {
            PhieuNhapChiTiets ct = new PhieuNhapChiTiets();
            BeanUtils.copyProperties(chiTiet, ct, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
            ct.setPhieuNhapMaPhieuNhap(pn.getId());
            ct.setSoLuong(BigDecimal.valueOf(Math.abs(chiTiet.getTonKho() - chiTiet.getThucTe())));
            ct.setGiaNhap(chiTiet.getDonGia());
            ct.setIsModified(false);
            ct.setRecordStatusId(RecordStatusContains.ACTIVE);
            tongTien += ct.getSoLuong().doubleValue() * ct.getGiaNhap().doubleValue();
            pn.getChiTiets().add(ct);
        }
        pn.setTongTien(tongTien);
        pn = hdrRepo.save(pn);
        this.phieuNhapChiTietsRepository.saveAll(pn.getChiTiets());
        updateInventory(pn);
        return pn;
    }

    @Override
    public PhieuNhaps updateByPhieuKiemKes(PhieuKiemKes e) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        PhieuNhaps phieuNhaps = detail(e.getPhieuNhapMaPhieuNhap());
        if (phieuNhaps == null) {
            throw new Exception("Không tìm thấy phiếu nhập cũ!");
        }
        delete(phieuNhaps.getId());
        PhieuNhaps pn = new PhieuNhaps();
        BeanUtils.copyProperties(e, pn, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
        PhieuNhaps init = init(ENoteType.InventoryAdjustment.longValue(), null);
        pn.setSoPhieuNhap(init.getSoPhieuNhap());
        pn.setNhaThuocMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        pn.setStoreId(userInfo.getNhaThuoc().getId());
        pn.setTargetId(null);
        pn.setTargetStoreId(null);
        pn.setTargetManagementId(null);
        pn.setRecordStatusId(RecordStatusContains.ACTIVE);
        pn.setIsModified(false);
        pn.setCreated(new Date());
        pn.setCreatedByUserId(getLoggedUser().getId());
        pn.setDaTra(0d);
        pn.setTongTien(0d);
        pn.setVat(0);
        pn = hdrRepo.save(pn);
        // save chi tiết
        pn.setChiTiets(new ArrayList<>());
        Double tongTien = 0d;
        for (PhieuKiemKeChiTiets chiTiet : e.getChiTiets()) {
            PhieuNhapChiTiets ct = new PhieuNhapChiTiets();
            BeanUtils.copyProperties(chiTiet, ct, "id", "created", "createdByUserId", "modified", "modifiedByUserId", "recordStatusId");
            ct.setPhieuNhapMaPhieuNhap(pn.getId());
            ct.setSoLuong(BigDecimal.valueOf(Math.abs(chiTiet.getTonKho() - chiTiet.getThucTe())));
            ct.setGiaNhap(chiTiet.getDonGia());
            ct.setIsModified(false);
            ct.setRecordStatusId(RecordStatusContains.ACTIVE);
            tongTien += ct.getSoLuong().doubleValue() * ct.getGiaNhap().doubleValue();
            pn.getChiTiets().add(ct);
        }
        pn.setTongTien(tongTien);
        pn = hdrRepo.save(pn);
        this.phieuNhapChiTietsRepository.saveAll(pn.getChiTiets());
        updateInventory(pn);
        return pn;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        PhieuNhaps phieuNhaps = detail(id);
        phieuNhaps.setRecordStatusId(RecordStatusContains.DELETED);
        hdrRepo.save(phieuNhaps);
        updateInventory(phieuNhaps);
        return true;
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
