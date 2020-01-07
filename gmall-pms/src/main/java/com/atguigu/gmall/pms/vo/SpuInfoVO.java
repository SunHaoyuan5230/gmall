package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @Auther: SunHaoyuan
 * @Date: 2020/1/7 08:51
 * @Description:
 */

@Data
public class SpuInfoVO extends SpuInfoEntity {

    private List<String> spuImages; // spu描述信息（图片）

    private List<BaseAttrValueVO> baseAttrs; // 通用规格属性

    private List<SkuInfoVO> skus;
}