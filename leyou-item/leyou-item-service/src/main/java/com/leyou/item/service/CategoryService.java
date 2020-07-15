package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
/**
 * @className
 * @Description TODO 根据父节点查询子节点
 * @param pid
 * @return java.util.List<com.leyou.item.pojo.Category>
 **/
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }
    public List<Category>  queryByBrandId(Long bid) {
        List<Category>categories=this.categoryMapper.queryByBrandId(bid);
        return categories;
    }

    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> list = this.categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category category : list) {
            names.add(category.getName());
        }
        return names;

        // return list.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
}
