package com.smarterp.system.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.system.service.IExtApService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExtApServiceImpl implements IExtApService {
    @Override
    public JSONObject getRateInfo(String fromSate, String toSate) {
        Map<String, Object> map = new HashMap<>();
        map.put("from",fromSate);
        map.put("to",toSate);
        map.put("app_id","iivlkttglgrzrozk");
        map.put("app_secret","Z0RyRUk5dTY3d1Y1K0hzQk1nWUlsZz09");
        String res = HttpUtil.get("https://www.mxnzp.com/api/exchange_rate/aim",map);
        return JSONObject.parseObject(res);
    }
}
