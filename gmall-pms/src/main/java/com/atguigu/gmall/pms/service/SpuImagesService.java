package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuImagesEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu图片
 *
 * @author SunHaoyuan
 * @email yuan5230@outlook.com
 * @date 2020-01-03 18:41:55
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageVo queryPage(QueryCondition params);
}

