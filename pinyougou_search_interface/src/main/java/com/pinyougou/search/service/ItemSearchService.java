package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索
     */
    public Map<String , Object> search(Map searchMap);
}
