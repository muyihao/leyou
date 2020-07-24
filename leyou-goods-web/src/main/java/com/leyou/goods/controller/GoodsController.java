package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller

public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    /**
     * 跳转到商品详情页
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model) {
        Map<String, Object> map = this.goodsService.loadData(id);
        model.addAllAttributes(map);
        //将map中的每个键值对加在了model中，key就作为属性名称，value为属性数据。返回到前端页面取数据时可以直接根据属性名获取数据
//        this.goodsHtmlService.createHtml(id);
        this.goodsHtmlService.asyncExcute(id);//也就是creatHtml方法的多线程版本
        return "item";
    }
}

