package com.leyou.goods.service;

import com.leyou.common.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;
    /**
     * 创建html页面,把静态文件生成到服务器本地
     *
     * @param spuId
     * @throws Exception
     */
    public void createHtml(Long spuId) {
        // 创建thymeleaf上下文对象
        Context context = new Context();
        // 获取页面数据
        Map<String, Object> map = this.goodsService.loadData(spuId);
        // 把数据放入上下文对象
        context.setVariables(map);
        PrintWriter printWriter = null;
        try {
            // 创建输出流,
            //这里静态化存放的位置应该和访问路劲相同，访问路劲item/75.html，则应该放在html目录下的item文件夹下
            printWriter=new PrintWriter(new File("D:\\Develop\\nginx-1.14.0\\html\\item\\"+spuId+".html"));
            // 执行页面静态化方法
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (printWriter!=null)
            printWriter.close();
        }
    }
    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    public void deleteHtml(Long id) {
        File file = new File("D:\\Develop\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
