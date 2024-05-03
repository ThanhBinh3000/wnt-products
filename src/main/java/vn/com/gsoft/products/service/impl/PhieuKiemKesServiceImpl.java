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

import java.util.Optional;


@Service
@Log4j2
public class PhieuKiemKesServiceImpl extends BaseServiceImpl<PhieuKiemKes, PhieuKiemKesReq, Long> implements PhieuKiemKesService {

    private PhieuKiemKesRepository hdrRepo;
    private PhieuKiemKeChiTietsRepository phieuKiemKeChiTietsRepository;
    private UserProfileRepository userProfileRepository;
    private ThuocsRepository thuocsRepository;
    private NhomThuocsRepository nhomThuocsRepository;

    @Autowired
    public PhieuKiemKesServiceImpl(PhieuKiemKesRepository hdrRepo, UserProfileRepository userProfileRepository,
                                   PhieuKiemKeChiTietsRepository phieuKiemKeChiTietsRepository,
                                   ThuocsRepository thuocsRepository,
                                   NhomThuocsRepository nhomThuocsRepository) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.userProfileRepository = userProfileRepository;
        this.phieuKiemKeChiTietsRepository = phieuKiemKeChiTietsRepository;
        this.thuocsRepository = thuocsRepository;
        this.nhomThuocsRepository =nhomThuocsRepository;
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
        for(PhieuKiemKeChiTiets ct: phieuKiemKes.getChiTiets()){
            if(ct.getThuocThuocId() != null && ct.getThuocThuocId()>0){
                Optional<Thuocs> thuocs = thuocsRepository.findById(ct.getThuocThuocId());
                if(thuocs.isPresent()){
                    ct.setMaThuoc(thuocs.get().getMaThuoc());
                    ct.setTenThuoc(thuocs.get().getTenThuoc());
                    if(thuocs.get().getNhomThuocMaNhomThuoc() != null && thuocs.get().getNhomThuocMaNhomThuoc() >0){
                        Optional<NhomThuocs> nhomThuocs = nhomThuocsRepository.findById(thuocs.get().getNhomThuocMaNhomThuoc());
                        nhomThuocs.ifPresent(value -> ct.setTenNhomThuoc(value.getTenNhomThuoc()));
                    }
                }
            }
        }
        return phieuKiemKes;
    }
}
