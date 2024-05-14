package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.service.PhieuKiemKesService;
import vn.com.gsoft.products.service.PhieuNhapsService;
import vn.com.gsoft.products.service.PhieuXuatsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Log4j2
public class PhieuKiemKesServiceImpl extends BaseServiceImpl<PhieuKiemKes, PhieuKiemKesReq, Long> implements PhieuKiemKesService {

    private PhieuKiemKesRepository hdrRepo;
    private PhieuKiemKeChiTietsRepository phieuKiemKeChiTietsRepository;
    private UserProfileRepository userProfileRepository;
    private ThuocsRepository thuocsRepository;
    private NhomThuocsRepository nhomThuocsRepository;
    private PhieuXuatChiTietsRepository phieuXuatChiTietsRepository;
    private PhieuNhapChiTietsRepository phieuNhapChiTietsRepository;
    private PhieuXuatsService phieuXuatsService;
    private PhieuNhapsService phieuNhapsService;

    @Autowired
    public PhieuKiemKesServiceImpl(PhieuKiemKesRepository hdrRepo, UserProfileRepository userProfileRepository,
                                   PhieuKiemKeChiTietsRepository phieuKiemKeChiTietsRepository,
                                   ThuocsRepository thuocsRepository,
                                   PhieuXuatChiTietsRepository phieuXuatChiTietsRepository,
                                   PhieuNhapChiTietsRepository phieuNhapChiTietsRepository,
                                   PhieuXuatsService phieuXuatsService,
                                   PhieuNhapsService phieuNhapsService,
                                   NhomThuocsRepository nhomThuocsRepository) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.userProfileRepository = userProfileRepository;
        this.phieuKiemKeChiTietsRepository = phieuKiemKeChiTietsRepository;
        this.thuocsRepository = thuocsRepository;
        this.nhomThuocsRepository = nhomThuocsRepository;
        this.phieuXuatChiTietsRepository = phieuXuatChiTietsRepository;
        this.phieuNhapChiTietsRepository = phieuNhapChiTietsRepository;
        this.phieuXuatsService = phieuXuatsService;
        this.phieuNhapsService = phieuNhapsService;
    }

    @Override
    public Page<PhieuKiemKes> searchPage(PhieuKiemKesReq req) throws Exception {
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        Page<PhieuKiemKes> phieuKiemKes = hdrRepo.searchPage(req, pageable);
        for (PhieuKiemKes kk : phieuKiemKes.getContent()) {
            if (kk.getCreatedByUserId() != null && kk.getCreatedByUserId() > 0) {
                Optional<UserProfile> userProfile = userProfileRepository.findById(kk.getCreatedByUserId());
                userProfile.ifPresent(profile -> kk.setCreatedByUseText(profile.getTenDayDu()));
            }
        }
        return phieuKiemKes;
    }

    @Override
    public PhieuKiemKes create(PhieuKiemKesReq req) throws Exception {
        // todo
        return super.create(req);
    }

    @Override
    public PhieuKiemKes update(PhieuKiemKesReq req) throws Exception {
        // todo
        return super.update(req);
    }

    @Override
    public PhieuKiemKes detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<PhieuKiemKes> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        PhieuKiemKes phieuKiemKes = optional.get();
        if (phieuKiemKes.getCreatedByUserId() != null && phieuKiemKes.getCreatedByUserId() > 0) {
            Optional<UserProfile> userProfile = userProfileRepository.findById(phieuKiemKes.getCreatedByUserId());
            userProfile.ifPresent(profile -> phieuKiemKes.setCreatedByUseText(profile.getTenDayDu()));
        }
        phieuKiemKes.setChiTiets(phieuKiemKeChiTietsRepository.findByPhieuKiemKeMaPhieuKiemKe(phieuKiemKes.getId()));
        for (PhieuKiemKeChiTiets ct : phieuKiemKes.getChiTiets()) {
            if (ct.getThuocThuocId() != null && ct.getThuocThuocId() > 0) {
                Optional<Thuocs> thuocs = thuocsRepository.findById(ct.getThuocThuocId());
                if (thuocs.isPresent()) {
                    ct.setMaThuoc(thuocs.get().getMaThuoc());
                    ct.setTenThuoc(thuocs.get().getTenThuoc());
                    if (thuocs.get().getNhomThuocMaNhomThuoc() != null && thuocs.get().getNhomThuocMaNhomThuoc() > 0) {
                        Optional<NhomThuocs> nhomThuocs = nhomThuocsRepository.findById(thuocs.get().getNhomThuocMaNhomThuoc());
                        nhomThuocs.ifPresent(value -> ct.setTenNhomThuoc(value.getTenNhomThuoc()));
                    }
                }
            }
        }
        return phieuKiemKes;
    }

    @Override
    public PhieuKiemKes canKho(PhieuKiemKesReq req) throws Exception {
        PhieuKiemKes phieuKiemKes = null;
        if (req.getId() != null && req.getId() > 0) {
            phieuKiemKes = super.create(req);
        } else {
            phieuKiemKes = super.update(req);
        }
        // xử lý cân kho
        List<PhieuKiemKeChiTiets> canXuat = new ArrayList<>();
        List<PhieuKiemKeChiTiets> canNhap = new ArrayList<>();
        for (PhieuKiemKeChiTiets ct : phieuKiemKes.getChiTiets()) {
            if (ct.getThucTe() > ct.getTonKho()) {
                canNhap.add(ct);
            }
            if (ct.getThucTe() < ct.getTonKho()) {
                canXuat.add(ct);
            }
        }
        if (!canXuat.isEmpty()) {
            PhieuXuats phieuXuats = null;
            phieuKiemKes.setChiTiets(canXuat);
            if (req.getPhieuXuatMaPhieuXuat() != null && req.getPhieuXuatMaPhieuXuat() > 0) {
                phieuXuats = phieuXuatsService.updateByPhieuKiemKes(phieuKiemKes);
            } else {
                phieuXuats = phieuXuatsService.createByPhieuKiemKes(phieuKiemKes);
            }
            req.setPhieuXuatMaPhieuXuat(phieuXuats != null ? phieuXuats.getId() : null);
            super.update(req);
        }
        if (!canNhap.isEmpty()) {
            PhieuNhaps phieuNhaps = null;
            phieuKiemKes.setChiTiets(canNhap);
            if (req.getPhieuNhapMaPhieuNhap() != null && req.getPhieuNhapMaPhieuNhap() > 0) {
                phieuNhaps = phieuNhapsService.updateByPhieuKiemKes(phieuKiemKes);
            } else {
                phieuNhaps = phieuNhapsService.createByPhieuKiemKes(phieuKiemKes);
            }
            req.setPhieuNhapMaPhieuNhap(phieuNhaps != null ? phieuNhaps.getId() : null);
            super.update(req);
        }
        return phieuKiemKes;
    }

    @Override
    public Boolean checkThuocTonTaiKiemKe(Long thuocThuocId) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        return !hdrRepo.findByMaNhaThuocAndThuocThuocIdAndDaCanKhoAndRecordStatusId(userInfo.getNhaThuoc().getMaNhaThuoc(), thuocThuocId, false, RecordStatusContains.ACTIVE).isEmpty();
    }

    @Override
    public List<PhieuKiemKeChiTiets> checkBienDong(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        PhieuKiemKes detail = detail(id);
        List<PhieuKiemKeChiTiets> results = new ArrayList<>();
        List<PhieuKiemKeChiTiets> phieuKiemKeChiTiet = detail.getChiTiets();
        for (PhieuKiemKeChiTiets ct : phieuKiemKeChiTiet) {
            List<PhieuXuatChiTiets> exitPhieuXuat = phieuXuatChiTietsRepository.findByMaNhaThuocAndThuocThuocIdAndCreatedAndAndRecordStatusId(userInfo.getNhaThuoc().getMaNhaThuoc(), ct.getThuocThuocId(), detail.getCreated(), RecordStatusContains.ACTIVE);
            if (!exitPhieuXuat.isEmpty()) {
                results.add(ct);
                continue;
            }
            List<PhieuNhapChiTiets> exitPhieuNhap = phieuNhapChiTietsRepository.findByMaNhaThuocAndThuocThuocIdAndCreatedAndAndRecordStatusId(userInfo.getNhaThuoc().getMaNhaThuoc(), ct.getThuocThuocId(), detail.getCreated(), RecordStatusContains.ACTIVE);
            if (!exitPhieuNhap.isEmpty()) {
                results.add(ct);
            }
        }
        return results;
    }
}
