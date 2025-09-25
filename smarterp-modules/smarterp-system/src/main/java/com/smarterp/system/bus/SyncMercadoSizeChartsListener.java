package com.smarterp.system.bus;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.domain.MercadoSizeCharts;
import com.smarterp.system.mapper.MercadoSizeChartsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;


@Component
public class SyncMercadoSizeChartsListener {

    private static final Logger logger = LoggerFactory.getLogger(SyncMercadoSizeChartsListener.class);

    @Resource
    private RedisService redisService;

    @Resource
    private MercadoSizeChartsMapper mercadoSizeChartsMapper;

    /**
     * 同步尺码表数据
     *
     * @param mercadoSizeCharts
     */
    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(
            name = RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
            type = ExchangeTypes.TOPIC),
            value = @Queue(name = RabbitMQConstant.MercadoQueue.SYNC_MERCADO_SIZE_CHARTS_DATA_QUEUE),
            key = RabbitMQConstant.RoutingKey.SYNC_MERCADO_SIZE_CHARTS_DATA))
    public void syncMercadoSizeChartsListener(MercadoSizeCharts mercadoSizeCharts) {
        try {
            // 获取尺码表信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("domain_id", mercadoSizeCharts.getDomainId());
            jsonObject.put("site_id", "CBT");
            jsonObject.put("type", "SPECIFIC");
            jsonObject.put("seller_id", mercadoSizeCharts.getSellerId());

            JSONArray attributes = new JSONArray();
            JSONObject attribute = new JSONObject();
            attribute.put("id", "GENDER");

            JSONArray values = new JSONArray();
            JSONObject value = new JSONObject();
            value.put("id", mercadoSizeCharts.getGenderId());
            values.add(value);
            attribute.put("values", values);
            attributes.add(attribute);
            jsonObject.put("attributes", attributes);

            String accessToken = redisService.getCacheObject("ACCESS_TOKEN_SHOP:" + mercadoSizeCharts.getSellerId());
            JSONObject sizeChart = MercadoHttpUtil.getSizeChart(jsonObject, accessToken);
            logger.error("从后端获取的美客多尺码表信息 {}", sizeChart.toJSONString());
            if (sizeChart != null) {
                JSONArray charts = sizeChart.getJSONArray("charts");
                if (charts != null && charts.size() > 0) {
                    for (int index = 0; index < charts.size(); index++) {
                        JSONObject chart = charts.getJSONObject(index);

                        MercadoSizeCharts sizeCharts = new MercadoSizeCharts();
                        sizeCharts.setId(chart.getString("id"));
                        sizeCharts.setNames(chart.getJSONObject("names").toJSONString());
                        sizeCharts.setDomainId(chart.getString("domain_id"));
                        String genderId = "";
                        String genderName = "";
                        JSONArray attributeList = chart.getJSONArray("attributes");
                        if (attributeList != null && attributeList.size() > 0) {
                            for (int j = 0; j < attributeList.size(); j++) {
                                JSONObject object = attributeList.getJSONObject(j);
                                if ("GENDER".equals(object.getString("id"))) {
                                    JSONArray jsonArray = object.getJSONArray("values");
                                    if (jsonArray != null && jsonArray.size() > 0) {
                                        genderId = jsonArray.getJSONObject(0).getString("id");
                                        genderName = jsonArray.getJSONObject(0).getString("name");
                                    }
                                }
                            }
                        }
                        sizeCharts.setGenderId(genderId);
                        sizeCharts.setGenderName(genderName);
                        sizeCharts.setSiteId(chart.getString("site_id"));
                        sizeCharts.setType(chart.getString("type"));
                        sizeCharts.setSellerId(chart.getString("seller_id"));
                        sizeCharts.setMeasureType(chart.getString("measure_type"));
                        sizeCharts.setMainAttributeId(chart.getString("main_attribute_id"));
                        sizeCharts.setSizeChartInfo(chart.toJSONString());
                        sizeCharts.setCreatedTime(new Date());
                        mercadoSizeChartsMapper.insertMercadoSizeCharts(sizeCharts);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("采集链接异常 {}", e.getMessage());
        }
    }

}
