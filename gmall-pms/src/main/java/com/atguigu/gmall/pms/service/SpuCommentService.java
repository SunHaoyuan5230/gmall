package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuCommentEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品评价
 *
 * @author SunHaoyuan
 * @email yuan5230@outlook.com
 * @date 2020-01-03 18:41:55
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageVo queryPage(QueryCondition params);
}

