package com.smarterp.order.domain.dto;

import java.util.List;

public class MercadoSizeChartDetail {

    private String id;

    private String name;

    private String mainAttributeId;

    List<MercadoSizeChart> rows;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainAttributeId() {
        return mainAttributeId;
    }

    public void setMainAttributeId(String mainAttributeId) {
        this.mainAttributeId = mainAttributeId;
    }

    public List<MercadoSizeChart> getRows() {
        return rows;
    }

    public void setRows(List<MercadoSizeChart> rows) {
        this.rows = rows;
    }
}
