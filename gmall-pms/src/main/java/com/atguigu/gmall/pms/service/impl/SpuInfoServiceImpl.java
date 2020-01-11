package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private SpuInfoDescService descService;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuByCidOrKey(QueryCondition condition, Long catId) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        // 查询条件
        // 判断查全站还是查本类 0--查全站，!0--查本类
        if (catId != 0l) {
            wrapper.eq("catalog_id", catId);
        }

        // 关键字查询
        String key = condition.getKey();
        if (StringUtils.isNotBlank(key)) {
            // 箭头函数为lambda表达式，wrapperT 意为wrapper，因为外部已定义，所以加了T，or()是或者
            wrapper.and(wrapperT -> wrapperT.eq("id", key).or().like("spu_name", key));
        }


        // 分页
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Transactional()
    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {

        // 1.保存spu相关信息
        // 1.1 spuInfo
        Long spuId = saveSpuInfo(spuInfoVO);

        // 1.2 spuInfoDesc   spu描述信息
        descService.saveSpuDesc(spuInfoVO, spuId);

        // 1.3 基础属性相关信息
        saveBaseAttrValue(spuInfoVO, spuId);

        // 2.保存sku相关信息
        saveSkuAndSales(spuInfoVO, spuId);

        // int a = 1/0;

    }


    private void saveSkuAndSales(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skus)) {
            return;
        }

        skus.forEach(sku ->{
            // 2.1 skuInfo
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, skuInfoEntity);
            List<String> images = sku.getImages();
            if (!CollectionUtils.isEmpty(images)) {
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg() == null ?
                        images.get(0) : skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            skuInfoDao.insert(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            // 2.2 skuInfoImages
            if (!CollectionUtils.isEmpty(images)) {
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    imagesEntity.setSkuId(skuId);
                    imagesEntity.setDefaultImg(StringUtils.equals(image, skuInfoEntity.getSkuDefaultImg()) ? 1 : 0);
                    imagesEntity.setImgSort(0);
                    imagesEntity.setImgUrl(image);
                    return imagesEntity;
                }).collect(Collectors.toList());

                imagesService.saveBatch(skuImagesEntities);
            }

            // 2.3 skuSaleAttrValue
            List<SkuSaleAttrValueEntity> saleAttrs = sku.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)) {
                saleAttrs.forEach(skuSaleAttrValueEntity -> {
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    skuSaleAttrValueEntity.setAttrSort(0);
                });
                saleAttrValueService.saveBatch(saleAttrs);
            }
            // 3.营销相关信息

            SaleVO saleVO = new SaleVO();
            BeanUtils.copyProperties(sku, saleVO);
            saleVO.setSkuId(skuId);
            smsClient.saveSales(saleVO);

            // 3.1 skuBounds积分
            // 3.2 skuLadder打折
            // 3.3 FullReduction满减
        });
    }

    private void saveBaseAttrValue(SpuInfoVO spuInfoVO, Long spuId) {
        List<BaseAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> attrValues = baseAttrs.stream().map(baseAttrValueVO -> {
                ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
                BeanUtils.copyProperties(baseAttrValueVO, attrValueEntity);
                attrValueEntity.setSpuId(spuId);
                attrValueEntity.setAttrSort(0);
                attrValueEntity.setQuickShow(0);
                return attrValueEntity;
            }).collect(Collectors.toList());
            attrValueService.saveBatch(attrValues);
        }
    }

    private Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        save(spuInfoVO);
        return spuInfoVO.getId();
    }

}