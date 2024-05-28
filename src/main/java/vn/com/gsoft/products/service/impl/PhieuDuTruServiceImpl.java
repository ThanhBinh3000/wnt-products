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
import vn.com.gsoft.products.model.dto.PhieuDuTruReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.service.PhieuDuTruService;
import vn.com.gsoft.products.util.system.FileUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
@Log4j2
public class PhieuDuTruServiceImpl extends BaseServiceImpl<PhieuDuTru, PhieuDuTruReq, Long> implements PhieuDuTruService {

    private final NhaThuocsRepository nhaThuocsRepository;
    private PhieuDuTruRepository hdrRepo;
    private PhieuDuTruChiTietRepository dtlRepo;
    private UserProfileRepository userProfileRepository;
    private NhaCungCapsRepository nhaCungCapsRepository;
    private ThuocsRepository thuocsRepository;
    private NhomThuocsRepository nhomThuocsRepository;
    private DonViTinhsRepository donViTinhsRepository;

    @Autowired
    public PhieuDuTruServiceImpl(PhieuDuTruRepository hdrRepo, UserProfileRepository userProfileRepository,
                                 NhaCungCapsRepository nhaCungCapsRepository,
                                 PhieuDuTruChiTietRepository phieuDuTruChiTietRepository,
                                 ThuocsRepository thuocsRepository,
                                 NhomThuocsRepository nhomThuocsRepository,
                                 DonViTinhsRepository donViTinhsRepository, NhaThuocsRepository nhaThuocsRepository) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.userProfileRepository = userProfileRepository;
        this.nhaCungCapsRepository = nhaCungCapsRepository;
        this.dtlRepo = phieuDuTruChiTietRepository;
        this.thuocsRepository = thuocsRepository;
        this.nhomThuocsRepository = nhomThuocsRepository;
        this.donViTinhsRepository = donViTinhsRepository;
        this.nhaThuocsRepository = nhaThuocsRepository;
    }

    @Override
    public Page<PhieuDuTru> searchPage(PhieuDuTruReq req) throws Exception {
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        Page<PhieuDuTru> phieuDuTrus = hdrRepo.searchPage(req, pageable);
        for (PhieuDuTru kk : phieuDuTrus.getContent()) {
            if (kk.getCreatedByUserId() != null && kk.getCreatedByUserId() > 0) {
                Optional<UserProfile> userProfile = userProfileRepository.findById(kk.getCreatedByUserId());
                userProfile.ifPresent(profile -> kk.setCreatedByUseText(profile.getTenDayDu()));
            }
            if (kk.getSupplierId() != null && kk.getSupplierId() > 0) {
                Optional<NhaCungCaps> nhaCungCaps = nhaCungCapsRepository.findById(kk.getSupplierId());
                nhaCungCaps.ifPresent(profile -> kk.setSupplierText(nhaCungCaps.get().getTenNhaCungCap()));
            }
        }
        return phieuDuTrus;
    }

    @Override
    public PhieuDuTru create(PhieuDuTruReq req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        req.setMaNhaThuoc(userInfo.getNhaThuoc().getMaNhaThuoc());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        req.setSupplierId(userInfo.getNhaThuoc().getId());
        PhieuDuTru hdr = new PhieuDuTru();
        BeanUtils.copyProperties(req, hdr, "id");
        hdr.setCreated(new Date());
        hdr.setCreatedByUserId(getLoggedUser().getId());
        PhieuDuTru save = hdrRepo.save(hdr);
        List<PhieuDuTruChiTiet> phieuNhapChiTiets = saveChildren(save.getId(), req);
        save.setChiTiets(phieuNhapChiTiets);
        return save;
    }

    private List<PhieuDuTruChiTiet> saveChildren(Long idHdr, PhieuDuTruReq req) {
        // save chi tiết
        dtlRepo.deleteAllByMaPhieuDuTru(idHdr);
        for (PhieuDuTruChiTiet chiTiet : req.getChiTiets()) {
            chiTiet.setMaPhieuDuTru(idHdr);
            chiTiet.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        this.dtlRepo.saveAll(req.getChiTiets());
        return req.getChiTiets();
    }

    @Override
    public PhieuDuTru detail(Long id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<PhieuDuTru> optional = hdrRepo.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        } else {
            if (optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE) {
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        PhieuDuTru phieuDuTru = optional.get();
        if (phieuDuTru.getCreatedByUserId() != null && phieuDuTru.getCreatedByUserId() > 0) {
            Optional<UserProfile> userProfile = userProfileRepository.findById(phieuDuTru.getCreatedByUserId());
            userProfile.ifPresent(profile -> phieuDuTru.setCreatedByUseText(profile.getTenDayDu()));
        }
        if (phieuDuTru.getSupplierId() != null && phieuDuTru.getSupplierId() > 0) {
            Optional<NhaCungCaps> nhaCungCaps = nhaCungCapsRepository.findById(phieuDuTru.getSupplierId());
            nhaCungCaps.ifPresent(profile -> phieuDuTru.setSupplierText(nhaCungCaps.get().getTenNhaCungCap()));
        }
        if (phieuDuTru.getMaNhaThuoc() != null) {
            NhaThuocs nhaThuocs = nhaThuocsRepository.findByMaNhaThuoc(phieuDuTru.getMaNhaThuoc());
            if (nhaThuocs != null) {
                phieuDuTru.setMaNhaThuocText(nhaThuocs.getTenNhaThuoc());
                phieuDuTru.setDiaChiNhaThuoc(nhaThuocs.getDiaChi());
                phieuDuTru.setSdtNhaThuoc(nhaThuocs.getDienThoai());
            }
        }
        phieuDuTru.setChiTiets(dtlRepo.findByMaPhieuDuTru(phieuDuTru.getId()));
        for (PhieuDuTruChiTiet ct : phieuDuTru.getChiTiets()) {
            if (ct.getMaThuoc() != null && ct.getMaThuoc() > 0) {
                Optional<Thuocs> thuocs = thuocsRepository.findById(ct.getMaThuoc());
                if (thuocs.isPresent()) {
                    ct.setMaThuocText(thuocs.get().getMaThuoc());
                    ct.setTenThuocText(thuocs.get().getTenThuoc());
                    if (thuocs.get().getNhomThuocMaNhomThuoc() != null && thuocs.get().getNhomThuocMaNhomThuoc() > 0) {
                        Optional<NhomThuocs> nhomThuocs = nhomThuocsRepository.findById(thuocs.get().getNhomThuocMaNhomThuoc());
                        nhomThuocs.ifPresent(value -> ct.setTenNhomThuoc(value.getTenNhomThuoc()));
                    }
                }
            }
            if (ct.getMaDonViDuTru() != null && ct.getMaDonViDuTru() > 0) {
                Optional<DonViTinhs> donViTinhs = donViTinhsRepository.findById(ct.getMaDonViDuTru());
                donViTinhs.ifPresent(viTinhs -> ct.setMaDonViDuTruText(viTinhs.getTenDonViTinh()));
            }
            if (ct.getMaDonViTon() != null && ct.getMaDonViTon() > 0) {
                Optional<DonViTinhs> donViTinhs = donViTinhsRepository.findById(ct.getMaDonViTon());
                donViTinhs.ifPresent(viTinhs -> ct.setMaDonViTonText(viTinhs.getTenDonViTinh()));
            }
        }
        return phieuDuTru;
    }

    @Override
    public ReportTemplateResponse preview(HashMap<String, Object> hashMap) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        try {
            PhieuDuTru phieuDuTru = this.detail(FileUtils.safeToLong(hashMap.get("id")));
            String templatePath = "/template/phieuDuTru/phieu_du_tru.docx";
            InputStream templateInputStream = FileUtils.templateInputStream(templatePath);
            return FileUtils.convertDocxToPdf(templateInputStream, phieuDuTru);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}