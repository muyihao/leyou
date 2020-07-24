package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchListener {
    @Autowired
    private SearchService searchService;

    /**
     * 接收消息，若新增商品则根据spuId新增索引
     * @param id
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.SAVE.QUEUE",durable = "true"),
 exchange =   @Exchange(value = "LEYOU.ITEM.EXCHANGE",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true" ),
            key = {"item.insert","item.update"}))
    public void save(Long id) throws IOException {
        if(id==null)
            return;
        this.searchService.save(id);
    }

    /**
     * 若删除商品则删除其索引
     * @param id
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.DELETE.QUEUE",durable = "true"),
            exchange =   @Exchange(value = "LEYOU.ITEM.EXCHANGE",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true" ),
            key = {"item.delete"}))
    public void delete(Long id) throws IOException {
        if(id==null)
            return;

        this.searchService.delete(id);
    }
}
