package com.smarterp.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.mercado.MercadoHttpUtil;
import com.smarterp.common.core.utils.mercado.MercadoSecureUtil;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.datascope.annotation.DataScope;
import com.smarterp.common.redis.service.RedisService;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.system.api.domain.SysUser;
import com.smarterp.system.constant.RabbitMQConstant;
import com.smarterp.system.domain.MercadoShopInfo;
import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.domain.vo.MercadoShopInfoVO;
import com.smarterp.system.mapper.MercadoShopInfoMapper;
import com.smarterp.system.mapper.MercadoShopInfoSiteMapper;
import com.smarterp.system.mapper.SysUserMapper;
import com.smarterp.system.service.IMercadoShopInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 店铺Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-19
 */
@Service
public class MercadoShopInfoServiceImpl implements IMercadoShopInfoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoShopInfoServiceImpl.class);

    @Resource
    private MercadoShopInfoMapper mercadoShopInfoMapper;

    @Resource
    private MercadoShopInfoSiteMapper mercadoShopInfoSiteMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private RedisService redisService;

    /**
     * 查询店铺
     *
     * @param id 店铺主键
     * @return 店铺
     */
    @Override
    public MercadoShopInfo selectMercadoShopInfoById(Long id) {
        return mercadoShopInfoMapper.selectMercadoShopInfoById(id);
    }

    /**
     * 查询店铺列表
     *
     * @param mercadoShopInfo 店铺
     * @return 店铺
     */
    @Override
    @DataScope(deptAlias = "dept", userAlias = "su")
    public List<MercadoShopInfo> selectMercadoShopInfoList(MercadoShopInfo mercadoShopInfo) {
        List<MercadoShopInfo> mercadoShopInfos = mercadoShopInfoMapper.selectMercadoShopInfoList(mercadoShopInfo);
        if (!mercadoShopInfos.isEmpty()) {
            for (MercadoShopInfo shop : mercadoShopInfos) {
                Long deptId = shop.getDeptId();
                // 价格系数
                String priceRatio = redisService.getJSONStringByKey("DEPT_PRICE_RATIO" + deptId);
                if (priceRatio != null) {
                    shop.setPriceRatio(priceRatio);
                } else {
                    shop.setPriceRatio("1.15");
                }
            }
        }
        return mercadoShopInfos;
    }

    /**
     * 新增店铺
     *
     * @param mercadoShopInfoVO 店铺
     * @return 结果
     */
    @Override
    public AjaxResult insertMercadoShopInfo(MercadoShopInfoVO mercadoShopInfoVO) {
        // 获取当前登录人信息
        SysUser currentUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        if (currentUser == null) {
            return AjaxResult.error("当前用户登录已失效，请重新登录");
        }
        MercadoShopInfo example = new MercadoShopInfo();
        example.setId(IdUtil.getSnowflake(1, 1).nextId());
        example.setDeptId(currentUser.getDeptId());
        // 获取accessToken信息
        JSONObject token = MercadoHttpUtil.getAccessToken(mercadoShopInfoVO.getCode());
        if (token == null) {
            return AjaxResult.error("授权失败，请重新授权");
        }
        example.setMerchantId(token.getString("user_id"));
        example.setAccessToken(MercadoSecureUtil.encryptMercado(String.valueOf(example.getId()), token.getString("access_token")));
        example.setRefreshToken(MercadoSecureUtil.encryptMercado(String.valueOf(example.getId()), token.getString("refresh_token")));
        // 根据merchantId判断是否为重复绑定
        MercadoShopInfo model = mercadoShopInfoMapper.selectMercadoShopByMerchantId(example.getMerchantId());
        if (model != null) {
            return AjaxResult.error("此店铺已经绑定授权，请勿重新绑定");
        }
        // 根据accessToken获取店铺信息
        JSONObject shopInfo = MercadoHttpUtil.getShopInfo(token.getString("access_token"));
        if (shopInfo == null) {
            return AjaxResult.error("获取店铺信息失败，请重新授权");
        }
        example.setMercadoShopName(shopInfo.getString("mercadoShopName"));
        example.setAliasName(shopInfo.getString("aliasName"));
        example.setAuthorizeStatus("Yes");
        example.setAuthorizeTime(new Date());
        example.setRemark(mercadoShopInfoVO.getRemark());
        example.setCreateBy(String.valueOf(currentUser.getUserId()));
        example.setCreateTime(new Date());
        example.setUpdateBy(String.valueOf(currentUser.getUserId()));
        example.setUpdateTime(new Date());
        example.setDeleted(false);
        int result = mercadoShopInfoMapper.insertMercadoShopInfo(example);
        if (result > 0) {
            // 使用消息队列处理店铺站点数据
            amqpTemplate.convertAndSend(RabbitMQConstant.MQ_PULL_MERCADO_PRODUCT_EXCHANGE,
                    RabbitMQConstant.RoutingKey.GET_MERCADO_SHOP_ITEM_INFO, example);
        }
        return result > 0 ? AjaxResult.success("绑定店铺成功") : AjaxResult.error("绑定店铺失败");
    }

    /**
     * 修改店铺
     *
     * @param mercadoShopInfo 店铺
     * @return 结果
     */
    @Override
    public int updateMercadoShopInfo(MercadoShopInfo mercadoShopInfo) {
        return mercadoShopInfoMapper.updateMercadoShopInfo(mercadoShopInfo);
    }

    /**
     * 批量删除店铺
     *
     * @param ids 需要删除的店铺主键
     * @return 结果
     */
    @Override
    public int deleteMercadoShopInfoByIds(Long[] ids) {
        return mercadoShopInfoMapper.deleteMercadoShopInfoByIds(ids);
    }

    /**
     * 删除店铺信息
     *
     * @param id 店铺主键
     * @return 结果
     */
    @Override
    public int deleteMercadoShopInfoById(Long id) {
        MercadoShopInfo mercadoShopInfo = mercadoShopInfoMapper.selectMercadoShopInfoById(id);
        int result = mercadoShopInfoMapper.deleteMercadoShopInfoById(id);
        if (result > 0) {
            if (mercadoShopInfo != null) {
                mercadoShopInfoSiteMapper.deleteSiteByMerchantId(mercadoShopInfo.getMerchantId());
            }
        }
        return result;
    }

    @Override
    public List<MercadoShopInfoSite> selectMercadoShopInfoSiteList(MercadoShopInfoSite mercadoShopInfoSite) {
        return mercadoShopInfoSiteMapper.selectMercadoShopInfoSiteList(mercadoShopInfoSite);
    }

    @Override
    public int editPriceRatio(MercadoShopInfo mercadoShopInfo) {
        redisService.setCacheObject("DEPT_PRICE_RATIO" + mercadoShopInfo.getDeptId(), mercadoShopInfo.getPriceRatio());
        return 1;
    }
}
