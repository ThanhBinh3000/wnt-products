package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.SampleNoteReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.service.SampleNoteService;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


@Service
@Log4j2
public class SampleNoteServiceImpl extends BaseServiceImpl<SampleNote, SampleNoteReq, Long> implements SampleNoteService {

    private SampleNoteRepository hdrRepo;
    private SampleNoteDetailRepository sampleNoteDetailRepository;
    private KhachHangsRepository khachHangsRepository;
    private BacSiesRepository bacSiesRepository;
    private ThuocsRepository thuocsRepository;
    private DonViTinhsRepository donViTinhsRepository;

    @Autowired
    public SampleNoteServiceImpl(SampleNoteRepository hdrRepo, KhachHangsRepository khachHangsRepository,
                                 BacSiesRepository bacSiesRepository, SampleNoteDetailRepository sampleNoteDetailRepository,
                                 ThuocsRepository thuocsRepository,
                                 DonViTinhsRepository donViTinhsRepository) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.khachHangsRepository = khachHangsRepository;
        this.bacSiesRepository = bacSiesRepository;
        this.sampleNoteDetailRepository = sampleNoteDetailRepository;
        this.thuocsRepository = thuocsRepository;
        this.donViTinhsRepository = donViTinhsRepository;
    }

    @Override
    public Page<SampleNote> searchPage(SampleNoteReq req) throws Exception {
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        Page<SampleNote> sampleNotes = hdrRepo.searchPage(req, pageable);
        for (SampleNote sn : sampleNotes.getContent()) {
            if (sn.getDoctorId() != null && sn.getDoctorId() > 0) {
                Optional<BacSies> bacSies = bacSiesRepository.findById(sn.getDoctorId());
                if (bacSies.isPresent()) {
                    sn.setDoctorName(bacSies.get().getTenBacSy());
                    sn.setDoctorPhoneNumber(bacSies.get().getDienThoai());
                }
            }
            if (sn.getPatientId() != null && sn.getPatientId() > 0) {
                Optional<KhachHangs> khachHangs = khachHangsRepository.findById(sn.getPatientId());
                if (khachHangs.isPresent()) {
                    sn.setPatientName(khachHangs.get().getTenKhachHang());
                    sn.setPatientPhoneNumber(khachHangs.get().getSoDienThoai());
                }
            }
            sn.setTypeDrugTotal(sampleNoteDetailRepository.countByNoteID(sn.getId()));
        }
        return sampleNotes;
    }

    @Override
    public SampleNote detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<SampleNote> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        SampleNote sampleNote = optional.get();
        if (sampleNote.getDoctorId() != null && sampleNote.getDoctorId() > 0) {
            Optional<BacSies> bacSies = bacSiesRepository.findById(sampleNote.getDoctorId());
            if (bacSies.isPresent()) {
                sampleNote.setDoctorName(bacSies.get().getTenBacSy());
                sampleNote.setDoctorPhoneNumber(bacSies.get().getDienThoai());
            }
        }
        if (sampleNote.getPatientId() != null && sampleNote.getPatientId() > 0) {
            Optional<KhachHangs> khachHangs = khachHangsRepository.findById(sampleNote.getPatientId());
            if (khachHangs.isPresent()) {
                sampleNote.setPatientName(khachHangs.get().getTenKhachHang());
                sampleNote.setPatientPhoneNumber(khachHangs.get().getSoDienThoai());
            }
        }
        sampleNote.setTypeDrugTotal(sampleNoteDetailRepository.countByNoteID(sampleNote.getId()));
        sampleNote.setChiTiets(sampleNoteDetailRepository.findByNoteID(sampleNote.getId()));
        for (SampleNoteDetail ct : sampleNote.getChiTiets()) {
            if (ct.getDrugID() != null && ct.getDrugID() > 0) {
                Optional<Thuocs> thuocs = thuocsRepository.findById(ct.getDrugID());
                if (thuocs.isPresent()) {
                    ct.setDrugCodeText(thuocs.get().getMaThuoc());
                    ct.setDrugNameText(thuocs.get().getTenThuoc());
                }

            }
            if (ct.getDrugUnitID() != null && ct.getDrugUnitID() > 0) {
                Optional<DonViTinhs> donViTinhs = donViTinhsRepository.findById(ct.getDrugUnitID());
                if (donViTinhs.isPresent()) {
                    ct.setDrugUnitText(donViTinhs.get().getTenDonViTinh());
                }

            }
        }
        return optional.get();
    }

    @Override
    public SampleNote create(SampleNoteReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        SampleNote e = new SampleNote();
        BeanUtils.copyProperties(req, e, "id");
        if (e.getRecordStatusId() == null) {
            e.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        if (e.getAmount() == null) {
            e.setAmount(new BigDecimal(0l));
        }
        e.setDrugStoreID(userInfo.getNhaThuoc().getMaNhaThuoc());
        e.setStoreId(userInfo.getNhaThuoc().getId());
        e.setCreatedDateTime(new Date());
        e.setCreatedByUserID(getLoggedUser().getId());
        e.setCreated(new Date());
        e.setCreatedByUserId(getLoggedUser().getId());
        e = hdrRepo.save(e);
        for (SampleNoteDetail ct : e.getChiTiets()) {
            ct.setNoteID(e.getId());
            ct.setDrugStoreID(e.getDrugStoreID());
            ct.setStoreId(e.getStoreId());
        }
        sampleNoteDetailRepository.saveAll(e.getChiTiets());
        return e;
    }

    @Override
    public SampleNote update(SampleNoteReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<SampleNote> optional = hdrRepo.findById(req.getId());
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }

        SampleNote e = optional.get();
        BeanUtils.copyProperties(req, e, "id", "created", "createdByUserId");
        if (e.getRecordStatusId() == null) {
            e.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        if (e.getAmount() == null) {
            e.setAmount(new BigDecimal(0l));
        }
        e.setDrugStoreID(userInfo.getNhaThuoc().getMaNhaThuoc());
        e.setStoreId(userInfo.getNhaThuoc().getId());
        e.setModifiedDateTime(new Date());
        e.setModifiedByUserID(getLoggedUser().getId());
        e.setModified(new Date());
        e.setModifiedByUserId(getLoggedUser().getId());
        e = hdrRepo.save(e);
        sampleNoteDetailRepository.deleteAllByNoteID(e.getId());
        for (SampleNoteDetail ct : e.getChiTiets()) {
            ct.setNoteID(e.getId());
            ct.setDrugStoreID(e.getDrugStoreID());
            ct.setStoreId(e.getStoreId());
        }
        sampleNoteDetailRepository.saveAll(e.getChiTiets());
        return e;
    }
}
