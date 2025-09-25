package com.smarterp.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class MercadoConfigInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置名称")
    @Size(max = 50, message = "配置名称长度不能超过50")
    private String configName;

    @ApiModelProperty(value = "配置Key")
    @Size(max = 100, message = "配置KEY长度不能超过100")
    private String configKey;

    @ApiModelProperty(value = "配置Value")
    private String configValue;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
