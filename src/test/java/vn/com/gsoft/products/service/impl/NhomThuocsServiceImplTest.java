package vn.com.gsoft.products.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.com.gsoft.products.entity.NhomThuocs;
import vn.com.gsoft.products.entity.PhieuDuTru;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.dto.PhieuDuTruReq;
import vn.com.gsoft.products.model.system.NhaThuocs;
import vn.com.gsoft.products.model.system.PaggingReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.service.NhomThuocsService;
import vn.com.gsoft.products.service.PhieuDuTruService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class NhomThuocsServiceImplTest {
    @Autowired
    private NhomThuocsService nhomThuocsService;
    @BeforeAll
    static void beforeAll() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Profile p = new Profile();
        NhaThuocs nt =new NhaThuocs();
        nt.setMaNhaThuoc("0010");
        p.setNhaThuoc(nt);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(p, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Test
    void searchPage() throws Exception {
        NhomThuocsReq nhomThuocsReq = new NhomThuocsReq();
        PaggingReq paggingReq = new PaggingReq();
        paggingReq.setPage(0);
        paggingReq.setLimit(10);
        nhomThuocsReq.setPaggingReq(paggingReq);
        Page<NhomThuocs> sampleNotes = nhomThuocsService.searchPage(nhomThuocsReq);
        assert sampleNotes != null;
    }
}