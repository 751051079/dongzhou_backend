package com.smarterp.order.service;

import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.order.domain.MercadoProductItem;
import com.smarterp.order.domain.dto.MercadoInfoDto;

import java.util.List;

/**
 * 产品，店铺和站点信息关联Service接口
 *
 * @author smarterp
 * @date 2023-04-26
 */
public interface IMercadoProductTraService {
    List<Long> releaseProduct(MercadoInfoDto mercadoInfoDto);
}
