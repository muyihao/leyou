package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;


    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") long cid) {
        List<SpecGroup> specGroups = this.specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(specGroups);

    }

    /**
     * 查找参数集合
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */


    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(@RequestParam(value = "gid", required = false) Long gid, @RequestParam(value = "cid", required = false) Long cid,
                                                       @RequestParam(value = "generic", required = false) Boolean generic, @RequestParam(value = "searching", required = false) Boolean searching) {
        List<SpecParam> specParams = this.specificationService.queryParams(gid,cid ,generic,searching);
        if (CollectionUtils.isEmpty(specParams)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParams);
    }

}
