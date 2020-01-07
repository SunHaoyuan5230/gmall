package com.atguigu.gmall.pms.vo;

import com.alibaba.nacos.client.naming.utils.StringUtils;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Auther: SunHaoyuan
 * @Date: 2020/1/7 11:45
 * @Description:
 */

@Data
public class BaseAttrValueVO extends ProductAttrValueEntity {


    public void setValueSelected(List<String> valueSelected) {

        if (!CollectionUtils.isEmpty(valueSelected)) {
            this.setAttrValue(StringUtils.join(valueSelected, ","));
        } else {
            this.setAttrValue(null);
        }
    }

}