package vn.com.gsoft.products.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.gsoft.products.constant.PathContains;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;
import vn.com.gsoft.products.model.system.BaseResponse;
import vn.com.gsoft.products.service.PhieuKiemKesService;
import vn.com.gsoft.products.util.system.ResponseUtils;


@Slf4j
@RestController
@RequestMapping("/phieu-kiem-kes")
public class PhieuKiemKesController {

    @Autowired
    PhieuKiemKesService service;


    @PostMapping(value = PathContains.URL_SEARCH_PAGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colection(@RequestBody PhieuKiemKesReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.searchPage(objReq)));
    }


    @PostMapping(value = PathContains.URL_SEARCH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colectionList(@RequestBody PhieuKiemKesReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.searchList(objReq)));
    }


    @PostMapping(value = PathContains.URL_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> insert(@Valid @RequestBody PhieuKiemKesReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.create(objReq)));
    }


    @PostMapping(value = PathContains.URL_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> update(@Valid @RequestBody PhieuKiemKesReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.update(objReq)));
    }
    @PostMapping(value = PathContains.URL_UPDATE+"-han-dung", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> updateHanDung(@Valid @RequestBody PhieuKiemKesReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.updateHanDung(objReq.getId(),objReq.getDonGia(),objReq.getSoLo(),objReq.getHanDung())));
    }

    @GetMapping(value = PathContains.URL_DETAIL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> detail(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.detail(id)));
    }


    @PostMapping(value = PathContains.URL_DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> delete(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.delete(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_DELETE+"-chi-tiet", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> deleteChiTiet(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.deleteChiTiet(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_DELETE_DATABASE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> deleteDatabase(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.deleteForever(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_UPDATE_STATUS_MULTI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> updStatusMulti(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.updateStatusMulti(idSearchReq)));
    }

    @PostMapping(value = PathContains.URL_RESTORE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> restore(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.restore(idSearchReq.getId())));
    }

    @PostMapping(value = "can-kho", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> canKho(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.canKho(idSearchReq)));
    }

    @PostMapping(value = "check-thuoc-ton-tai-kiem-ke", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> checkThuocTonTaiKiemKe(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.checkThuocTonTaiKiemKe(idSearchReq.getThuocThuocId())));
    }

    @PostMapping(value = "check-bien-dong", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> checkBienDong(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.checkBienDong(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_SEARCH_PAGE+"-not-in-kiem-ke", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colectionNotInKiemKe(@Valid @RequestBody PhieuKiemKesReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.colectionNotInKiemKe(idSearchReq.getFromDate(), idSearchReq.getToDate())));
    }
}
