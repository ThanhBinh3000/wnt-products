package vn.com.gsoft.products.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.com.gsoft.products.constant.PathContains;
import vn.com.gsoft.products.model.dto.DraftListDrugReq;
import vn.com.gsoft.products.model.dto.FileDto;
import vn.com.gsoft.products.model.dto.ThuocsReq;
import vn.com.gsoft.products.model.dto.dataBarcode;
import vn.com.gsoft.products.model.system.BaseResponse;
import vn.com.gsoft.products.service.ThuocsService;
import vn.com.gsoft.products.util.system.ResponseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = PathContains.URL_THUOC)
@Slf4j

public class ThuocsController {

    @Autowired
    ThuocsService service;


    @PostMapping(value = PathContains.URL_SEARCH_PAGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colection(@RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.searchPage(objReq)));
    }

    @PostMapping(value = PathContains.URL_SEARCH_PAGE+"-sell", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colectionSell(@RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.searchPageSell(objReq)));
    }


    @PostMapping(value = PathContains.URL_SEARCH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colectionList(@RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.searchList(objReq)));
    }

    @PostMapping(value = PathContains.URL_SEARCH_PAGE + "-not-in-phieu-kiem-ke", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colectionListNotInPhieuKiemKe(@RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.colectionPageNotInPhieuKiemKe(objReq)));
    }

    @PostMapping(value = PathContains.URL_SEARCH_PAGE + "-hang-du-tru", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> colectionListHangDuTru(@RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.colectionPageHangDuTru(objReq)));
    }

    @PostMapping(value = PathContains.URL_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> insert(@Valid @RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.create(objReq)));
    }


    @PostMapping(value = PathContains.URL_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> update(@Valid @RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.update(objReq)));
    }

    @PostMapping(value = PathContains.URL_CREATE + "-dich-vu", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> insertDichVu(@Valid @RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.create(objReq, "dịch vụ")));
    }


    @PostMapping(value = PathContains.URL_UPDATE + "-dich-vu", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> updateDichVu(@Valid @RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.update(objReq, "dịch vụ")));
    }

    @GetMapping(value = PathContains.URL_DETAIL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> detail(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.detail(id)));
    }

    @PostMapping(value = PathContains.URL_DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> delete(@Valid @RequestBody ThuocsReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.delete(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_DELETE_DATABASE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> deleteDatabase(@Valid @RequestBody ThuocsReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.deleteForever(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_RESTORE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> restore(@Valid @RequestBody ThuocsReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.restore(idSearchReq.getId())));
    }

    @PostMapping(value = PathContains.URL_UPDATE_STATUS_MULTI, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> updStatusMulti(@Valid @RequestBody ThuocsReq idSearchReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.updateStatusMulti(idSearchReq)));
    }

    @PostMapping(value = PathContains.URL_GENERATEDRUGCODE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> generateDrugCode() throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.generateDrugCode()));
    }

    @PostMapping(value = PathContains.URL_GENERATEBARCODE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> generateBarCode() throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.generateBarCode()));
    }

    @PostMapping(value = PathContains.URL_UPLOAD_IMAGE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> uploadImage(@ModelAttribute FileDto req) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.uploadImage(req)));
    }

    @PostMapping(value = PathContains.URL_EXPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void exportList(@RequestBody ThuocsReq objReq, HttpServletResponse response) throws Exception {
        try {
            service.export(objReq, response);
        } catch (Exception e) {
            log.error("Kết xuất danh sách dánh  : {}", e);
            final Map<String, Object> body = new HashMap<>();
            body.put("statusCode", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            body.put("msg", e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);

        }
    }

    @GetMapping(value = PathContains.URL_DETAILLASTVALUEWAREHOUSE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> getDataDetailLastValueWarehouse(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.getDataDetailLastValueWarehouse(id)));
    }
    @PostMapping(value = PathContains.URL_PREVIEW + "-print", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> getDataBarcode(@RequestBody HashMap<String, Object> body) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.getDataBarcode(body)));
    }

    @PostMapping(value = PathContains.URL_PREVIEW, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> preview(@RequestBody List<dataBarcode> body) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.preview(body)));
    }

    @PostMapping(value = PathContains.URL_IMPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseResponse> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.importExcel(file)));
    }

    @PostMapping(value = PathContains.URL_UPDATE_DRUG_PRICE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> updateDrugPrice(@Valid @RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.updateDrugPrice(objReq)));
    }

    @PostMapping(value = PathContains.URL_UPDATE_DRUG_PRICE_FOR_CHILD_STORE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> updateDrugPriceForChildStore(@Valid @RequestBody ThuocsReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.updateDrugPriceForChildStore(objReq)));
    }

    @PostMapping(value = PathContains.URL_SAVE_DRAFT_LIST_DRUG, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponse> saveDraftListDrug(@Valid @RequestBody DraftListDrugReq objReq) throws Exception {
        return ResponseEntity.ok(ResponseUtils.ok(service.saveDraftListDrug(objReq)));
    }
}
