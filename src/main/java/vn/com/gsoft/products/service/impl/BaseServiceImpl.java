package vn.com.gsoft.products.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.com.gsoft.products.constant.RecordStatusContains;
import vn.com.gsoft.products.entity.BaseEntity;
import vn.com.gsoft.products.model.system.BaseRequest;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.repository.BaseRepository;
import vn.com.gsoft.products.service.BaseService;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BaseServiceImpl<E extends BaseEntity,R extends BaseRequest, PK extends Serializable> implements BaseService<E,R, PK> {
    private BaseRepository repository;

    public BaseServiceImpl(BaseRepository repository) {
        this.repository = repository;
    }

    public Profile getLoggedUser() throws Exception {
        try {
            return (Profile) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception ex) {
            throw new Exception("Token invalid!");
        }
    }
    @Override
    public Page<E> searchPage(R req) throws Exception {
        Pageable pageable = PageRequest.of(req.getPaggingReq().getPage(), req.getPaggingReq().getLimit());
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        return repository.searchPage(req, pageable);
    }

    @Override
    public List<E> searchList(R req) throws Exception {
        req.setRecordStatusId(RecordStatusContains.ACTIVE);
        return repository.searchList(req);
    }

    @Override
    public E create(R req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");
        E e = (E) ((Class)((ParameterizedType)this.getClass().
                getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
        BeanUtils.copyProperties(req, e, "id");
        if(e.getRecordStatusId() == null){
            e.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        repository.save(e);
        return e;
    }

    @Override
    public E update(R req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<E> optional = repository.findById(req.getId());
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }

        E e = optional.get();
        BeanUtils.copyProperties(req, e, "id");
        if(e.getRecordStatusId() == null){
            e.setRecordStatusId(RecordStatusContains.ACTIVE);
        }
        repository.save(e);
        return e;
    }

    @Override
    public E detail(PK id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<E> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }else {
            if(optional.get().getRecordStatusId() != RecordStatusContains.ACTIVE){
                throw new Exception("Không tìm thấy dữ liệu.");
            }
        }
        return optional.get();
    }

    @Override
    public boolean delete(PK id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<E> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }
        optional.get().setRecordStatusId(RecordStatusContains.DELETED);
        repository.save(optional.get());
        return true;
    }


    @Override
    public boolean restore(PK id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<E> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }
        optional.get().setRecordStatusId(RecordStatusContains.ACTIVE);
        repository.save(optional.get());
        return true;
    }

    @Override
    public boolean deleteDatabase(PK id) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null)
            throw new Exception("Bad request.");

        Optional<E> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new Exception("Không tìm thấy dữ liệu.");
        }
        repository.delete(optional.get());
        return true;
    }

    @Override
    public boolean updateStatusMulti(R req) throws Exception {
        Profile userInfo = this.getLoggedUser();
        if (userInfo == null){
            throw new Exception("Bad request.");
        }
        if(req == null || req.getListIds().isEmpty()){
            throw new Exception("Bad request.");
        }
        List<E> allByIdIn = repository.findAllByIdIn(req.getListIds());

        if(req.getRecordStatusId() == RecordStatusContains.DELETED_DATABASE){
            repository.deleteAll(allByIdIn);
        }else{
            allByIdIn.forEach(item -> {
                item.setRecordStatusId(req.getRecordStatusId());
            });
            repository.saveAll(allByIdIn);
        }
        return true;
    }
}
