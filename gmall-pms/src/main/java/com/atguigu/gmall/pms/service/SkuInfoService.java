package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * sku信息
 *
 * @author SunHaoyuan
 * @email yuan5230@outlook.com
 * @date 2020-01-03 18:41:55
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageVo queryPage(QueryCondition params);
}

