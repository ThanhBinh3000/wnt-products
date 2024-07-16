package vn.com.gsoft.products.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.gsoft.products.constant.ENoteType;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.*;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.dto.ReportImage;
import vn.com.gsoft.products.model.dto.SampleNoteReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.*;
import vn.com.gsoft.products.service.SampleNoteService;
import vn.com.gsoft.products.util.system.FileUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Log4j2
public class SampleNoteServiceImpl extends BaseServiceImpl<SampleNote, SampleNoteReq, Long> implements SampleNoteService {

    private SampleNoteRepository hdrRepo;
    private SampleNoteDetailRepository sampleNoteDetailRepository;
    private KhachHangsRepository khachHangsRepository;
    private BacSiesRepository bacSiesRepository;
    private ThuocsRepository thuocsRepository;
    private DonViTinhsRepository donViTinhsRepository;
    private InventoryRepository inventoryRepository;
    private ESDiagnoseRepository diagnoseRepository;
    private NoteMedicalsRepository noteMedicalsRepository;
    private UserProfileRepository userProfileRepository;
    private ConfigTemplateRepository configTemplateRepository;

    @Autowired
    public SampleNoteServiceImpl(SampleNoteRepository hdrRepo, KhachHangsRepository khachHangsRepository,
                                 BacSiesRepository bacSiesRepository, SampleNoteDetailRepository sampleNoteDetailRepository,
                                 ThuocsRepository thuocsRepository,
                                 InventoryRepository inventoryRepository,
                                 ESDiagnoseRepository diagnoseRepository,
                                 DonViTinhsRepository donViTinhsRepository,
                                 NoteMedicalsRepository noteMedicalsRepository,
                                 UserProfileRepository userProfileRepository,
                                 ConfigTemplateRepository configTemplateRepository) {
        super(hdrRepo);
        this.hdrRepo = hdrRepo;
        this.khachHangsRepository = khachHangsRepository;
        this.bacSiesRepository = bacSiesRepository;
        this.sampleNoteDetailRepository = sampleNoteDetailRepository;
        this.thuocsRepository = thuocsRepository;
        this.donViTinhsRepository = donViTinhsRepository;
        this.inventoryRepository = inventoryRepository;
        this.diagnoseRepository = diagnoseRepository;
        this.noteMedicalsRepository = noteMedicalsRepository;
        this.userProfileRepository = userProfileRepository;
        this.configTemplateRepository = configTemplateRepository;
    }

    @Override
    public Page<SampleNote> searchPage(SampleNoteReq req) throws Exception {
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        Profile userInfo = this.getLoggedUser();
        if (req.getDrugStoreID() == null) {
            req.setDrugStoreID(userInfo.getNhaThuoc().getMaNhaThuoc());
        }
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
                sampleNote.setDoctorAddress(bacSies.get().getDiaChi());
            }
        }
        if (sampleNote.getPatientId() != null && sampleNote.getPatientId() > 0) {
            Optional<KhachHangs> khachHangs = khachHangsRepository.findById(sampleNote.getPatientId());
            if (khachHangs.isPresent()) {
                sampleNote.setPatientName(khachHangs.get().getTenKhachHang());
                sampleNote.setPatientPhoneNumber(khachHangs.get().getSoDienThoai());
                sampleNote.setPatientAddress(khachHangs.get().getDiaChi());
                sampleNote.setPatientGender(khachHangs.get().getSexId() == 1 ? "Nữ" : "Nam");
                sampleNote.setHealthInsuranceNumber(khachHangs.get().getHealthInsuranceNumber());
                sampleNote.setCitizenIdentification(khachHangs.get().getCitizenIdentification());
                if (khachHangs.get().getBirthDate() != null) {
                    sampleNote.setPatientBirthDate(khachHangs.get().getBirthDate());
                    sampleNote.setPatientAge(Period.between(khachHangs.get().getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears());
                }
            }
        }
        if (sampleNote.getCreatedByUserId() != null && sampleNote.getCreatedByUserId() > 0) {
            optional.get().setCreatedByUserText(this.userProfileRepository.findById(optional.get().getCreatedByUserId()).get().getTenDayDu());
        }
        sampleNote.setTypeDrugTotal(sampleNoteDetailRepository.countByNoteID(sampleNote.getId()));
        sampleNote.setChiTiets(sampleNoteDetailRepository.findByNoteID(sampleNote.getId()));
        if (sampleNote.getDiagnosticIds() != null) {
            String[] diagnosticIds = sampleNote.getDiagnosticIds().split(",");
            List<Long> ids = Arrays.stream(diagnosticIds)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<ESDiagnose> diagnose = diagnoseRepository.findByIdIn(ids);
            sampleNote.setDiagnostics(diagnose);
        }
        for (SampleNoteDetail ct : sampleNote.getChiTiets()) {
            if (ct.getDrugUnitID() != null && ct.getDrugUnitID() > 0) {
                Optional<DonViTinhs> donViTinhs = donViTinhsRepository.findById(ct.getDrugUnitID());
                if (donViTinhs.isPresent()) {
                    ct.setDrugUnitText(donViTinhs.get().getTenDonViTinh());
                }

            }
            if (ct.getDrugID() != null && ct.getDrugID() > 0) {
                Optional<Thuocs> thuocsOpt = thuocsRepository.findById(ct.getDrugID());
                if (thuocsOpt.isPresent()) {
                    ct.setDrugCodeText(thuocsOpt.get().getMaThuoc());
                    ct.setDrugNameText(thuocsOpt.get().getTenThuoc());
                    ct.setRetailPrice(thuocsOpt.get().getGiaBanLe());
                    Thuocs thuocs = thuocsOpt.get();
                    List<DonViTinhs> dviTinh = new ArrayList<>();
                    if (thuocs.getDonViXuatLeMaDonViTinh() > 0) {
                        Optional<DonViTinhs> byId = donViTinhsRepository.findById(thuocs.getDonViXuatLeMaDonViTinh());
                        if (byId.isPresent()) {
                            byId.get().setFactor(1);
                            dviTinh.add(byId.get());
                            thuocs.setTenDonViTinhXuatLe(byId.get().getTenDonViTinh());
                        }
                    }
                    if (thuocs.getDonViThuNguyenMaDonViTinh() > 0 && !thuocs.getDonViThuNguyenMaDonViTinh().equals(thuocs.getDonViXuatLeMaDonViTinh())) {
                        Optional<DonViTinhs> byId = donViTinhsRepository.findById(thuocs.getDonViThuNguyenMaDonViTinh());
                        if (byId.isPresent()) {
                            byId.get().setFactor(thuocs.getHeSo());
                            dviTinh.add(byId.get());
                            thuocs.setTenDonViTinhThuNguyen(byId.get().getTenDonViTinh());
                        }
                    }
                    thuocs.setListDonViTinhs(dviTinh);
                    InventoryReq inventoryReq = new InventoryReq();
                    inventoryReq.setDrugID(thuocs.getId());
                    inventoryReq.setDrugStoreID(thuocs.getNhaThuocMaNhaThuoc());
                    inventoryReq.setRecordStatusId(RecordStatusContains.ACTIVE);
                    Optional<Inventory> inventory = inventoryRepository.searchDetail(inventoryReq);
                    inventory.ifPresent(thuocs::setInventory);
                    ct.setThuocs(thuocs);
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

    @Override
    public ReportTemplateResponse preview(HashMap<String, Object> hashMap) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        try {
            String loai = FileUtils.safeToString(hashMap.get("loai"));
            Object value = hashMap.get("amountPrint");
            Long amountPrint = (value == null || "".equals(value.toString())) ? 1L : FileUtils.safeToLong(value.toString());
            String templatePath = "/donMau/";
            Integer checkType = 0;
            SampleNote sampleNote = this.detail(FileUtils.safeToLong(hashMap.get("id")));
            boolean isConnectSampleNote = sampleNote.getStatusConnect() != null
                    && sampleNote.getStatusConnect() == 2L
                    && userInfo.getNhaThuoc().getIsConnectivity();
            if (loai.equals(FileUtils.InPhieuA4)) {
                checkType = handleInKhachQuen(sampleNote, isConnectSampleNote);
            }
            if (loai.equals(FileUtils.InPhieuA5)) {
                checkType = handleInKhachLeA5(userInfo, sampleNote, isConnectSampleNote);
            }
            Optional<ConfigTemplate> configTemplates = null;
            configTemplates = configTemplateRepository.findByMaNhaThuocAndPrintTypeAndMaLoaiAndType(sampleNote.getDrugStoreID(), loai, Long.valueOf(ENoteType.SampleForm), checkType);
            if (!configTemplates.isPresent()) {
                configTemplates = configTemplateRepository.findByPrintTypeAndMaLoaiAndType(loai, Long.valueOf(ENoteType.SampleForm), checkType);
            }
            if (configTemplates.isPresent()) {
                templatePath += configTemplates.get().getTemplateFileName();
            }
            sampleNote.setPharmacyName(userInfo.getNhaThuoc().getTenNhaThuoc());
            sampleNote.setPharmacyAddress(userInfo.getNhaThuoc().getDiaChi());
            sampleNote.setPharmacyPhoneNumber(userInfo.getNhaThuoc().getDienThoai());
            List<ReportImage> reportImage = new ArrayList<>();
            if ("10324".equals(sampleNote.getDrugStoreID())) {
                reportImage.add(new ReportImage("imageLogo_10324", "src/main/resources/template/imageLogo_10324.png"));
            }
            InputStream templateInputStream = FileUtils.getInputStreamByFileName(templatePath);
            return FileUtils.convertDocxToPdf(templateInputStream, sampleNote, sampleNote.getBarcode(), amountPrint, reportImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer handleInKhachQuen(SampleNote sampleNote, Boolean isConnectSampleNote) {
        Integer checkType = 0;
        sampleNote.setWeight("........kg");
        String noteDes = "- Khám lại xin mang theo đơn này." +
                "\n- Số điện thoại liên hệ:.................................................................................................................." +
                "\n- Tên bố hoặc mẹ của trẻ hoặc người đưa trẻ đến khám bệnh, chữa bệnh:................................";
        if (isConnectSampleNote) {
            checkType = ("n".equals(sampleNote.getTypeId()) || "h".equals(sampleNote.getTypeId())) ? 1 : 2;
            sampleNote.setTitle(sampleNote.getTypeId() != null ? sampleNote.getTypeId().toUpperCase() : "");
            sampleNote.setHealthInsuranceNumber(sampleNote.getHealthInsuranceNumber() != null ? sampleNote.getHealthInsuranceNumber() : "..................................");
            sampleNote.setCitizenIdentification(sampleNote.getCitizenIdentification() != null ? sampleNote.getCitizenIdentification() : "...................");
            if (sampleNote.getIdExamination() != null && sampleNote.getIdExamination() > 0) {
                noteMedicalsRepository.findById(sampleNote.getIdExamination()).ifPresent(noteMedicals -> {
                    sampleNote.setWeight(noteMedicals.getWeight() != null ? noteMedicals.getWeight() + " kg" : "........kg");
                    sampleNote.setNoteName(noteMedicals.getIdDiagnostic() != null && noteMedicals.getIdDiagnostic() > 0
                            ? sampleNote.getNoteName()
                            : "...................................................................................................................................." +
                            "\n........................................................................................................................................................");
                });
            }
        } else if (Arrays.asList("0010", "10324").contains(sampleNote.getDrugStoreID())) {
            noteDes = "Đơn này có giá trị mua, lĩnh thuốc trong thời hạn 5 ngày kể từ ngày kê đơn.";
            if (sampleNote.getIdExamination() != null && sampleNote.getIdExamination() > 0) {
                noteMedicalsRepository.findById(sampleNote.getIdExamination()).ifPresent(noteMedicals -> {
                    sampleNote.setHeartbeat(noteMedicals.getHeartbeat() != null ? noteMedicals.getHeartbeat() + " nhịp/phút" : "       nhịp/phút");
                    sampleNote.setTemperature(noteMedicals.getTemperature() != null ? noteMedicals.getTemperature() + " độ C" : "       độ C");
                    sampleNote.setWeight(noteMedicals.getWeight() != null ? noteMedicals.getWeight() + " kg" : "       kg");
                    sampleNote.setBloodPressure(noteMedicals.getBloodPressure() != null ? noteMedicals.getBloodPressure() + " mmHg" : "       mmHg");
                    sampleNote.setBreathing(noteMedicals.getBreathing() != null ? noteMedicals.getBreathing() + " nhịp/phút" : "       nhịp/phút");
                    sampleNote.setConclude(noteMedicals.getConclude() != null ? noteMedicals.getConclude() : "");
                });
            }
        } else if (Arrays.asList("3220", "3780").contains(sampleNote.getDrugStoreID())) {
            sampleNote.setSizeDetail(sampleNote.getChiTiets().size());
            if (sampleNote.getTypeSampleNote() == 0L) {
                sampleNote.setTitle("ĐƠN THUỐC");
                sampleNote.setTitleTatle("Tên thuốc - Hàm lượng");
                sampleNote.setDescription("Khám lại xin mang theo đơn này" +
                        "\nĐơn vị tư vấn sử dụng thuốc (giờ hành chính): 19006422");
            } else {
                sampleNote.setTitle("PHIẾU TƯ VẤN");
                sampleNote.setTitleTatle("Thông tin - Hàm lượng");
                sampleNote.setDescription("Đọc kỹ mặt sau để biết thêm thông tin về phiếu tư vấn" +
                        "\nTrong quá trình sử dụng nếu có bất thường đến bệnh viện khám lại ngay hoặc liên hệ theo số điện thoại tư vấn (từ 7h đến 22h)" +
                        "\nSản phẩm này không phải là thuốc, không có tác dụng thay thế thuốc chữa bệnh");
            }
        } else {
            noteDes += "\n- Dùng thuốc đúng chỉ dẫn của bác sĩ, khám lại theo hẹn nếu có bất thường quay lại viện khám ngay." +
                    "\n- Đơn này có giá trị mua, lĩnh thuốc trong thời hạn 5 ngày kể từ ngày kê đơn." +
                    "\n- Khám lại xin mang theo đơn này.";
        }
        sampleNote.setNote(noteDes);
        return checkType;
    }

    private Integer handleInKhachLeA5(Profile userInfo, SampleNote sampleNote, Boolean isConnectSampleNote) {
        Integer checkType = 0;
        if (isConnectSampleNote) {
            checkType = ("n".equals(sampleNote.getTypeId()) || "h".equals(sampleNote.getTypeId())) ? 1 : 2;
            sampleNote.setTitle(sampleNote.getTypeId() != null ? sampleNote.getTypeId().toUpperCase() : "");
            sampleNote.setCitizenIdentification(sampleNote.getCitizenIdentification() != null ? sampleNote.getCitizenIdentification() : "...................");
            sampleNote.setHealthInsuranceNumber(sampleNote.getHealthInsuranceNumber() != null ? sampleNote.getHealthInsuranceNumber() : "..................................");
            if (sampleNote.getIdExamination() != null && sampleNote.getIdExamination() > 0) {
                noteMedicalsRepository.findById(sampleNote.getIdExamination()).ifPresent(noteMedicals -> {
                    sampleNote.setWeight(noteMedicals.getWeight() != null ? noteMedicals.getWeight() + " kg" : "........kg");
                    sampleNote.setNoteName(noteMedicals.getIdDiagnostic() != null && noteMedicals.getIdDiagnostic() > 0
                            ? sampleNote.getNoteName()
                            : ".........................................................................." +
                            "\n................................................................................................");
                });
            }
            sampleNote.setNote("- Khám lại xin mang theo đơn này." +
                    "\n- Số điện thoại liên hệ:....................................................................................." +
                    "\n- Tên bố hoặc mẹ của trẻ hoặc người đưa trẻ đến khám bệnh, chữa bệnh:" +
                    "\n......................................");
        } else if (userInfo.getApplicationSettings().stream().anyMatch(setting -> "ENABLE_DELIVERY_PICK_UP".equals(setting.getSettingKey()))) {
            checkType = 3;
        } else if (Arrays.asList("3220", "3780").contains(sampleNote.getDrugStoreID())) {
            sampleNote.setSizeDetail(sampleNote.getChiTiets().size());
            if (sampleNote.getTypeSampleNote() == 0L) {
                sampleNote.setTitle("ĐƠN THUỐC");
                sampleNote.setTitleTatle("Tên thuốc - Hàm lượng");
                sampleNote.setDescription("Khám lại xin mang theo đơn này" +
                        "\nĐơn vị tư vấn sử dụng thuốc (giờ hành chính): 19006422");
            } else {
                sampleNote.setTitle("PHIẾU TƯ VẤN");
                sampleNote.setTitleTatle("Thông tin - Hàm lượng");
                sampleNote.setDescription("Đọc kỹ mặt sau để biết thêm thông tin về phiếu tư vấn" +
                        "\nTrong quá trình sử dụng nếu có bất thường đến bệnh viện khám lại ngay hoặc liên hệ theo số điện thoại tư vấn (từ 7h đến 22h)" +
                        "\nSản phẩm này không phải là thuốc, không có tác dụng thay thế thuốc chữa bệnh");
            }
        }
        return checkType;
    }
}
