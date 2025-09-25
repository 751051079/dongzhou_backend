package com.smarterp.system.service;

import com.alibaba.fastjson2.JSONObject;

public interface IExtApService {
    JSONObject getRateInfo(String fromSate, String toSate);
}
