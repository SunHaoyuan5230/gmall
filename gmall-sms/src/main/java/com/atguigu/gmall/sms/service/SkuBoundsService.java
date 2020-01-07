package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.SaleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品sku积分设置
 *
 * @author SunHaoyuan
 * @email yuan5230@outlook.com
 * @date 2020-01-07 12:03:43
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saveSales(SaleVO saleVO);
}

