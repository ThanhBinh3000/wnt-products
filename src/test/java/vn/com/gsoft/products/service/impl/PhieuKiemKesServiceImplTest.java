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
import vn.com.gsoft.products.entity.PhieuKiemKeChiTiets;
import vn.com.gsoft.products.entity.PhieuKiemKes;
import vn.com.gsoft.products.entity.SampleNote;
import vn.com.gsoft.products.entity.Thuocs;
import vn.com.gsoft.products.model.dto.PhieuKiemKesReq;
import vn.com.gsoft.products.model.system.NhaThuocs;
import vn.com.gsoft.products.model.system.PaggingReq;
import vn.com.gsoft.products.model.system.Profile;
import vn.com.gsoft.products.service.PhieuKiemKesService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
class PhieuKiemKesServiceImplTest {
    @Autowired
    private PhieuKiemKesService phieuKiemKesService;

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
        PhieuKiemKesReq phieuKiemKesReq = new PhieuKiemKesReq();
        PaggingReq paggingReq = new PaggingReq();
        paggingReq.setPage(0);
        paggingReq.setLimit(10);
        phieuKiemKesReq.setPaggingReq(paggingReq);
        Page<PhieuKiemKes> sampleNotes = phieuKiemKesService.searchPage(phieuKiemKesReq);
        assert sampleNotes != null;
    }

    @Test
    void detail() throws Exception {
        PhieuKiemKes detail = phieuKiemKesService.detail(222981l);
        assert detail != null;
    }

    @Test
    void canKho() {
    }

    @Test
    void checkThuocTonTaiKiemKe() throws Exception {
        Boolean checkThuocTonTaiKiemKe = phieuKiemKesService.checkThuocTonTaiKiemKe(9681629l);
        assert checkThuocTonTaiKiemKe != null;
    }

    @Test
    void checkBienDong() throws Exception {
        List<PhieuKiemKeChiTiets> checkBienDong = phieuKiemKesService.checkBienDong(222782l);
        assert checkBienDong != null;
    }

    @Test
    void colectionNotInKiemKe() throws Exception {
        Date fromDate = new Date();
        fromDate.setMonth(3);
        List<Thuocs> thuocs = phieuKiemKesService.colectionNotInKiemKe(fromDate, new Date());
        assert thuocs != null;
    }
}