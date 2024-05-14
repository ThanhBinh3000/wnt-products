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
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.NhomThuocsReq;
import vn.com.gsoft.products.model.dto.ThuocsReq;
import vn.com.gsoft.products.model.system.NhaThuocs;
import vn.com.gsoft.products.model.system.PaggingReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.service.ThuocsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ThuocsServiceImplTest {
    @Autowired
    private ThuocsService thuocsService;

    @BeforeAll
    static void beforeAll() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Profile p = new Profile();
        NhaThuocs nt = new NhaThuocs();
        nt.setMaNhaThuoc("0010");
        p.setNhaThuoc(nt);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(p, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void colectionPageNotInPhieuKiemKe() throws Exception {
        ThuocsReq thuocsReq = new ThuocsReq();
        PaggingReq paggingReq = new PaggingReq();
        paggingReq.setPage(0);
        paggingReq.setLimit(10);
        thuocsReq.setPaggingReq(paggingReq);
        Page<Thuocs> sampleNotes = thuocsService.colectionPageNotInPhieuKiemKe(thuocsReq);
        assert sampleNotes != null;
    }
}