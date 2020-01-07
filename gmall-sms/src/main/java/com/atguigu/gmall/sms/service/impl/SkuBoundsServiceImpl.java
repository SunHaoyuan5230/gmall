package com.atguigu.gmall.sms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuLadderDao ladderDao;

    @Autowired
    private SkuFullReductionDao reductionDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSales(SaleVO saleVO) {
        // 3.营销相关信息
        // 3.1 skuBounds积分
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(saleVO, skuBoundsEntity);
        List<String> works = saleVO.getWork();
        skuBoundsEntity.setWork(new Integer(works.get(0)) + new Integer(works.get(1)) * 2 +
                new Integer(works.get(2)) * 4 + new Integer(works.get(3)) * 8);
        save(skuBoundsEntity);

        // 3.2 skuLadder打折
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(saleVO,ladderEntity);
        ladderEntity.setAddOther(saleVO.getLadderAddOther());
        ladderDao.insert(ladderEntity);

        // 3.3 FullReduction满减
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(saleVO,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(saleVO.getAddOther());
        reductionDao.insert(skuFullReductionEntity);


    }

}