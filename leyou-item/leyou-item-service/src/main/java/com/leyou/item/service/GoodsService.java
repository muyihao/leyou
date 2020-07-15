package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;

import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.voms.VOMSAttribute;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

//所有商品相关的业务（包括SPU和SKU）放到一个业务下：
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 搜索条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //过滤条件
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        // 分页条件
        PageHelper.startPage(page, rows);
        // 执行查询
        //因为这里是模糊查询，所以用参数example的select，而不是参数为对象的select
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        List<SpuBo> spuBos = new ArrayList<>();
        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            // copy共同属性的值到新的对象
            BeanUtils.copyProperties(spu, spuBo);
            // 查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));

            // 查询品牌的名称
            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());

            spuBos.add(spuBo);
        });

        return new PageResult<>(pageInfo.getTotal(), spuBos);

    }

    /**
     * //这里的逻辑比较复杂，我们除了要对SPU新增以外，还要对SpuDetail、Sku、Stock进行保存
     *
     * @param spuBo
     */
    @Transactional//开启事务
    public void saveGoods(SpuBo spuBo) {
        //先新增spu,spuBo是spu派生对象，可以作为spu看待,前端传过来封装的spuBo，spu的派生对象，有很多字段还是空值，需要去填补这些空值。
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);//spuBo是spu子类，这里可以作为spu传入，insertselective和insert基本一样，更高效些
        //新增spudetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insert(spuDetail);
        saveSkuAndStock(spuBo);
        //再新增sku
       /* List<Sku> skus = spuBo.getSkus();
        for (Sku sku : skus) {
            this.skuMapper.insert(sku);
        }*/

   /*
   抽取！
     spuBo.getSkus().forEach(sku -> {
           //新增sku,需要注意这里sku有些属性为空值
           sku.setId(null);
           sku.setSpuId(spuBo.getId());
           sku.setCreateTime(new Date());
           sku.setLastUpdateTime(sku.getCreateTime());
           this.skuMapper.insert(sku);
           //新增stock
           Stock stock = new Stock();
           stock.setSkuId(sku.getId());
           stock.setStock(sku.getStock());
           this.stockMapper.insertSelective(stock);
       });*/


    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        //顺带把sku里面的stock库存给查出来
        skus.forEach(s -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;

    }

    /**
     * 修改商品信息
     * spu数据可以修改，但是SKU数据无法修改，因为有可能之前存在的SKU现在已经不存在了，或者以前的sku属性都不存在了。
     * 比如以前内存有4G，现在没了。
     * 因此这里直接删除以前的SKU，然后新增即可。
     *
     * @param spuBo
     */
    @Transactional//添加事务，操作具有原子性
    public void updateGoods(SpuBo spuBo) {
//查询以前sku

        List<Sku> skus = this.querySkusBySpuId(spuBo.getId());//直接调用上面的方法
        //如果skus商品仍有存在的  ，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            //先删除stock
            Example example = new Example(Stock.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);
            //删除sku
            Sku sku = new Sku();
            sku.setSpuId(spuBo.getId());
            this.skuMapper.delete(sku);//注意这个下面这个的区别

         /*   Example exampleSku = new Example(Sku.class);
            Example.Criteria criteriaSku = exampleSku.createCriteria();
            criteria.andIn("Id", ids);
            this.skuMapper.deleteByExample(exampleSku);*/

        }
        //保存新的sku
        saveSkuAndStock(spuBo);

        //更新spu 的值
        spuBo.setSaleable(true);
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);//这些原来已有，不要覆盖
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.updateByPrimaryKeySelective(spuBo);//注意是selective，表示属性为空的就不会写入到sql语句中
        //更新spu详情
        this.spuDetailMapper.updateByPrimaryKey(spuBo.getSpuDetail());

    }
}
