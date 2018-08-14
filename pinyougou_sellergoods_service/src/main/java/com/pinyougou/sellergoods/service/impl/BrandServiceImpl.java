package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageResult result = new PageResult();
        PageHelper.startPage(pageNum,pageSize);
        List<TbBrand> brandList = brandMapper.selectByExample(null);
        PageInfo pageInfo = new PageInfo<>(brandList,pageSize);
        result.setRows(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        return result;
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        brandMapper.deleteByExample(example);
    }

    @Override
    public PageResult search(int pageNum, int pageSize, TbBrand brand) {
        PageResult result = new PageResult();
        //设置查询条件
        TbBrandExample example = new TbBrandExample();
        if(brand != null){
            TbBrandExample.Criteria criteria = example.createCriteria();
            if(StringUtils.isNotBlank(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(StringUtils.isNotBlank(brand.getFirstChar())){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> brandList = (Page<TbBrand>) brandMapper.selectByExample(example);
        result.setTotal(brandList.getTotal());
        result.setRows(brandList.getResult());
        return result;
    }
}
