package com.atguigu.gmall.search.respository;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Auther: SunHaoyuan
 * @Date: 2020/1/9 16:26
 * @Description:
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}