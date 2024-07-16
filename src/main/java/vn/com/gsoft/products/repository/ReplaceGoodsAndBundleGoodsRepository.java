package vn.com.gsoft.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.products.entity.Inventory;
import vn.com.gsoft.products.entity.ProductTypes;
import vn.com.gsoft.products.entity.ReplaceGoodsAndBundleGoods;
import vn.com.gsoft.products.model.dto.InventoryReq;
import vn.com.gsoft.products.model.dto.ProductTypesReq;
import vn.com.gsoft.products.model.dto.ReplaceGoodsAndBundleGoodsReq;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplaceGoodsAndBundleGoodsRepository extends BaseRepository<ReplaceGoodsAndBundleGoods, ReplaceGoodsAndBundleGoodsReq, Long> {
    @Query("SELECT c FROM ReplaceGoodsAndBundleGoods c " +
            " ORDER BY c.id desc"
    )
    Page<ReplaceGoodsAndBundleGoods> searchPage(@Param("param") ReplaceGoodsAndBundleGoodsReq param, Pageable pageable);


    @Query("SELECT c FROM ReplaceGoodsAndBundleGoods c " +
            " ORDER BY c.id desc"
    )
    List<ReplaceGoodsAndBundleGoods> searchList(@Param("param") ReplaceGoodsAndBundleGoodsReq param);
    List<ReplaceGoodsAndBundleGoods> findReplaceGoodsAndBundleGoodsByDrugStoreCodeAndDrugIdMap(String drugStoreCode, Long drugIdMap);
    List<ReplaceGoodsAndBundleGoods> findReplaceGoodsAndBundleGoodsByDrugStoreCodeAndDrugIdMapAndTypeId(String drugStoreCode, Long drugIdMap, Long typeId);
}
