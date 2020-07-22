package com.leyou.item.service;

import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecParamMapper specParamMapper;
    /**
     * 根据分类cid,查询该列别下的组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specificationMapper.select(specGroup);
    }
    /**
     * 查找参数集合
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid,Long cid ,Boolean generic,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.specParamMapper.select(specParam);
    }
    /**
     * 根据类别查询所有分组，以及每个分组下的具体参数
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> specGroups = this.queryGroupsByCid(cid);
        return specGroups.stream().map(specGroup -> {
            List<SpecParam> params = this.queryParams(specGroup.getId(), null, null, null);
            specGroup.setParams(params);
            return specGroup;
        }).collect(Collectors.toList());
    }
}
