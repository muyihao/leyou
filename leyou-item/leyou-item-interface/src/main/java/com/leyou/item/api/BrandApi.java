package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("brand")
public interface BrandApi {
    /**
     * 根据品牌id查询brand
     * @param id
     * @return
     */
    //注意上面的@RequestMapping("brand")这个表示路径，相当于额brand从roller上面的requestmapping
    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);
}
