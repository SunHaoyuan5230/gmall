package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.vo.AttrVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryByCidOrTypePage(QueryCondition condition, Long cid, Integer type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();

        // 构建查询条件
        // 根据cid查询
        wrapper.eq("catelog_Id",cid);
        // 判断type是否有值（因为默认为无值）
        if (type != null) {
            wrapper.eq("attr_type",type);
        }

        // 分页条件
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(condition), // 构建分页条件
                wrapper
        );


        return new PageVo(page);
    }

    @Override
    public void saveAttrVO(AttrVO attrVO) {

        // 新增attr
        save(attrVO);

        // 新增relation
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attrVO.getAttrGroupId());
        relationEntity.setAttrId(attrVO.getAttrId());
        relationDao.insert(relationEntity);

    }

}