package com.pinyougou.page.service;

/**
 * 商品详细页接口
 */
public interface ItemPageService {

    /**
     * 根据商品id生成商品信息页
     */
    public boolean genItemHtml(Long goodsId);
}
