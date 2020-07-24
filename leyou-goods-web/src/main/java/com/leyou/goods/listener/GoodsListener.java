package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    /**
     * 若新增或更新商品则新增或更新该商品的静态页面
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC), key = {"item.insert", "item.update"}//新增和更改相同的操作，更新阔以用新增直接覆盖

    ))
    public void save(Long id) {
        if (id == null) {
            return;
        }
        this.goodsHtmlService.createHtml(id);
    }

    /**
     * 若删除商品则删除对应的静态页面
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC), key = {"item.delete"}//新增和更改相同的操作，更新阔以用新增直接覆盖

    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        this.goodsHtmlService.deleteHtml(id);
    }
}
