package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author SunHaoyuan
 * @email yuan5230@outlook.com
 * @date 2020-01-03 18:41:55
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
