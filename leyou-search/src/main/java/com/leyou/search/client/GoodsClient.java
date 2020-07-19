package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public  interface GoodsClient extends GoodsApi{
 /*   *//**
     * 分页查询商品
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     *//*
    @GetMapping("spu/page")
    public abstract PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows
    );
    *//**
     * 根据spu商品id查询详情
     * @param spuId
     * @return
     *//*
    @GetMapping("spu/detail/{spuId}")
    public abstract SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);
    *//**
     * 根据spu的id查询skus
     * @param spuId
     * @return
     *//*
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);*/
}
