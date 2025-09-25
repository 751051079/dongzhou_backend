package com.smarterp.common.core.utils.mercado;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.vo.*;

import java.util.*;

/**
 * @author juntao.li
 * @ClassName MercadoCreateCbtItemUtil
 * @description 创建全局产品
 * @date 2023/4/25 22:42
 * @Version 1.0
 */
public class MercadoCreateCbtItemUtil {

    public static List<String> oneSize = Arrays.asList("0.49 kg","27 cm","10 cm","9 cm");

    public static List<String> twoSize = Arrays.asList("0.99 kg","35 cm","14 cm","10 cm");

    //创建全局产品
    public static JSONObject createItems(String token, MercaSendProDto dto, JSONObject cpItems) {
        if (dto.getPrice() == null) {
            dto.setPrice(cpItems.getBigDecimal("price"));
        }
        HttpRequest post = HttpUtil.createPost("https://api.mercadolibre.com/items");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        GrobItems items = new GrobItems();
        //添加基础信息
        items.setCategory_id(cpItems.getString("category_id"));
        items.setTitle(cpItems.getString("title"));
        if (cpItems.getJSONArray("variations").size() == 0) {
            items.setAvailable_quantity(999);
        } else {
            items.setAvailable_quantity(cpItems.getJSONArray("variations").size() * 99);
        }

        items.setPrice(dto.getPrice());
        List<Attributes> sales = new ArrayList<>();
        Attributes sale1 = new Attributes();
        sale1.setId("WARRANTY_TIME");
        sale1.setValue_name("30 days");
        sales.add(sale1);
        Attributes sale2 = new Attributes();
        sale2.setId("WARRANTY_TYPE");
        sale2.setValue_id("2230280");
        sale2.setValue_name("Seller warranty");
        sales.add(sale2);
        items.setSale_terms(sales);
        //添加图片信息
        List<ItemPictures> pictures = items.getPictures();
        JSONArray picturesArray = cpItems.getJSONArray("pictures");
        for (Object o : picturesArray) {
            JSONObject picJson = (JSONObject) o;
            pictures.add(ItemPictures.getInstance(picJson.getString("id")));
        }
        //添加属性
        List<Attributes> attributes = new ArrayList<>();
        JSONArray attributesCp = cpItems.getJSONArray("attributes");
        for (Object o : attributesCp) {
            JSONObject oJson = (JSONObject) o;
            if ("BRAND".equals(oJson.getString("id"))) {
                Attributes attribute = new Attributes();
                attribute.setId(oJson.getString("id"));
                attribute.setValue_name("Generic");
                attributes.add(attribute);
            } else if ("GTIN".equals(oJson.getString("id"))) {
                Attributes attribute = new Attributes();
                attribute.setId(oJson.getString("id"));
                attribute.setValue_name(dto.getUpc());
                attributes.add(attribute);
            } else if ("SELLER_SKU".equals(oJson.getString("id"))) {
                Attributes attribute = new Attributes();
                attribute.setId(oJson.getString("id"));
                attribute.setValue_name(dto.getSku());
                attributes.add(attribute);
            } else {
                Attributes attribute = new Attributes();
                attribute.setId(oJson.getString("id"));
                attribute.setValue_name(oJson.getString("value_name"));
                attributes.add(attribute);
            }
        }
        // }
        items.setAttributes(attributes);
        //添加变体
        JSONArray variationsCp = cpItems.getJSONArray("variations");
        List<Variations> variations = new ArrayList<>();
        for (Object o : variationsCp) {
            List<VariationsAttributeCombinations> attribute_combinations = new ArrayList<>();
            JSONObject oJson = (JSONObject) o;
            Variations variation = new Variations();
            variation.setPicture_ids(oJson.getJSONArray("picture_ids").toJavaList(String.class));
            variation.setAvailable_quantity(99);
            variation.setPrice(items.getPrice());
            JSONArray attribute_combinationsCp = oJson.getJSONArray("attribute_combinations");
            for (Object o1 : attribute_combinationsCp) {
                JSONObject o1Json = (JSONObject) o1;
                VariationsAttributeCombinations variationsAttributeCombinations = new VariationsAttributeCombinations();
                variationsAttributeCombinations.setId(o1Json.getString("id"));
                variationsAttributeCombinations.setValue_name(o1Json.getString("value_name"));
                attribute_combinations.add(variationsAttributeCombinations);
            }
            variation.setAttribute_combinations(attribute_combinations);
            List<Attributes> attributesVs = new ArrayList<>();
            Attributes attributesV = new Attributes();
            attributesV.setId("GTIN");
            attributesV.setValue_name(dto.getUpc());
            attributesVs.add(attributesV);
            Attributes attributesV2 = new Attributes();
            attributesV2.setId("SELLER_SKU");
            String sku = dto.getSku();
            for (VariationsAttributeCombinations v : attribute_combinations) {
                if ("COLOR".equals(v.getId()) || "SIZE".equals(v.getId())) {
                    String skuNew = sku + "_" + v.getValue_name();
                    if (skuNew.length() <= 30) {
                        sku = skuNew;
                    }
                }
            }
            attributesV2.setValue_name(sku);
            attributesVs.add(attributesV2);
            //添加尺寸
            Attributes attribute3 = new Attributes();
            attributesVs.add(attribute3);
            Attributes attribute4 = new Attributes();
            attributesVs.add(attribute4);
            Attributes attribute5 = new Attributes();
            attributesVs.add(attribute5);
            Attributes attribute6 = new Attributes();
            attributesVs.add(attribute6);
            attribute3.setId("PACKAGE_WEIGHT");
            attribute4.setId("PACKAGE_LENGTH");
            attribute5.setId("PACKAGE_WIDTH");
            attribute6.setId("PACKAGE_HEIGHT");
            if (dto.getWgType() == 1) {
                attribute3.setValue_name(oneSize.get(0));
                attribute4.setValue_name(oneSize.get(1));
                attribute5.setValue_name(oneSize.get(2));
                attribute6.setValue_name(oneSize.get(3));
            } else {
                attribute3.setValue_name(twoSize.get(0));
                attribute4.setValue_name(twoSize.get(1));
                attribute5.setValue_name(twoSize.get(2));
                attribute6.setValue_name(twoSize.get(3));
            }

            variation.setAttributes(attributesVs);
            variations.add(variation);
        }
        items.setVariations(variations);
        String res = post.addHeaders(headers).body(JSONObject.toJSONString(items)).execute().body();
        if (null != res) {
            return JSONObject.parseObject(res);
        } else {
            return null;
        }
    }
}
