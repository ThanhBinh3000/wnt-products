package vn.com.gsoft.products.controller;



import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.gsoft.products.constant.PathContains;
import vn.com.gsoft.products.model.dto.ThuocsReq;
import vn.com.gsoft.products.model.system.BaseResponse;
import vn.com.gsoft.products.service.ThuocsService;
import vn.com.gsoft.products.util.system.ResponseUtils;

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


  @PostMapping(value = PathContains.URL_SEARCH_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<BaseResponse> colectionList(@RequestBody ThuocsReq objReq) throws Exception {
    return ResponseEntity.ok(ResponseUtils.ok(service.searchList(objReq)));
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
    return ResponseEntity.ok(ResponseUtils.ok(service.deleteDatabase(idSearchReq.getId())));
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

}
