package com.atguigu.gmall.search;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValue;
import com.atguigu.gmall.search.respository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    void contextLoads() {
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
    }

    @Test
    void importData() {

        Long pageNum = 1l;
        Long pageSize = 100l;

        do {
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setLimit(pageSize);
            queryCondition.setPage(pageNum);
            // 分页查询spu
            Resp<List<SpuInfoEntity>> listResp = pmsClient.querySpuByPage(queryCondition);
            List<SpuInfoEntity> spuInfoEntities = listResp.getData();
            // 判断spu是否为空，若为空，说明在最后一页（例如1000条，刚好10页）
            if (CollectionUtils.isEmpty(spuInfoEntities)) {
                return;
            }

            // 遍历spu，获取sku导入es
            spuInfoEntities.forEach(spuInfoEntity -> {
                Resp<List<SkuInfoEntity>> skuResp = pmsClient.querySkusBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
                if (!CollectionUtils.isEmpty(skuInfoEntities)) {
                    List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                        Goods goods = new Goods();

                        // 查询库存信息
                        Resp<List<WareSkuEntity>> wareSkuResp = wmsClient.queryWareSkuBySkuId(skuInfoEntity.getSkuId());
                        List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                            goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
                        }
                        // 查询属性信息
                        Resp<List<ProductAttrValueEntity>> attrValue = pmsClient.querySearchAttrValue(spuInfoEntity.getId());
                        List<ProductAttrValueEntity> attrValueData = attrValue.getData();
                        attrValueData.stream().map(attrValueEntity -> {
                            SearchAttrValue searchAttrValue = new SearchAttrValue();
                            searchAttrValue.setAttrId(attrValueEntity.getAttrId());
                            searchAttrValue.setAttrName(attrValueEntity.getAttrName());
                            searchAttrValue.setAttrValue(attrValueEntity.getAttrValue());
                            return searchAttrValue;
                        });
                        goods.setAttrs(null);
                        // 查询品牌名
                        Resp<BrandEntity> brandEntityResp = pmsClient.queryBrandById(skuInfoEntity.getBrandId());
                        BrandEntity brandEntity = brandEntityResp.getData();
                        if (brandEntity != null) {
                            goods.setBrandId(skuInfoEntity.getBrandId());
                            goods.setBrandName(brandEntity.getName());
                        }
                        goods.setSkuId(skuInfoEntity.getSkuId());
                        goods.setSale(10l);
                        goods.setPrice(skuInfoEntity.getPrice().doubleValue());
                        goods.setCreateTime(spuInfoEntity.getCreateTime());
                        // 查询分类名
                        Resp<CategoryEntity> categoryEntityResp = pmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
                        CategoryEntity categoryEntity = categoryEntityResp.getData();
                        if (categoryEntity != null) {
                            goods.setCategoryId(skuInfoEntity.getCatalogId());
                            goods.setCategoryName(categoryEntity.getName());
                        }
                        goods.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                        goods.setSkuTitle(skuInfoEntity.getSkuTitle());
                        goods.setSkuSubTitle(skuInfoEntity.getSkuSubtitle());

                        return goods;
                    }).collect(Collectors.toList());
                    goodsRepository.saveAll(goodsList);
                }
            });

            pageSize = (long) spuInfoEntities.size();
            pageNum++;
        } while (pageSize == 100);
    }

}
