package com.smarterp.system.service.impl;

import com.smarterp.system.domain.MercadoShopInfoSite;
import com.smarterp.system.mapper.MercadoShopInfoSiteMapper;
import com.smarterp.system.service.IMercadoShopInfoSiteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 店铺站点信息Service业务层处理
 *
 * @author smarterp
 * @date 2024-06-06
 */
@Service
public class MercadoShopInfoSiteServiceImpl implements IMercadoShopInfoSiteService {

    @Resource
    private MercadoShopInfoSiteMapper mercadoShopInfoSiteMapper;

    /**
     * 查询店铺站点信息
     *
     * @param id 店铺站点信息主键
     * @return 店铺站点信息
     */
    @Override
    public MercadoShopInfoSite selectMercadoShopInfoSiteById(Long id) {
        return mercadoShopInfoSiteMapper.selectMercadoShopInfoSiteById(id);
    }

    /**
     * 查询店铺站点信息列表
     *
     * @param mercadoShopInfoSite 店铺站点信息
     * @return 店铺站点信息
     */
    @Override
    public List<MercadoShopInfoSite> selectMercadoShopInfoSiteList(MercadoShopInfoSite mercadoShopInfoSite) {
        return mercadoShopInfoSiteMapper.selectMercadoShopInfoSiteList(mercadoShopInfoSite);
    }

    /**
     * 新增店铺站点信息
     *
     * @param mercadoShopInfoSite 店铺站点信息
     * @return 结果
     */
    @Override
    public int insertMercadoShopInfoSite(MercadoShopInfoSite mercadoShopInfoSite) {
        mercadoShopInfoSite.setCreateTime(new Date());
        return mercadoShopInfoSiteMapper.insertMercadoShopInfoSite(mercadoShopInfoSite);
    }

    /**
     * 修改店铺站点信息
     *
     * @param mercadoShopInfoSite 店铺站点信息
     * @return 结果
     */
    @Override
    public int updateMercadoShopInfoSite(MercadoShopInfoSite mercadoShopInfoSite) {
        mercadoShopInfoSite.setUpdateTime(new Date());
        return mercadoShopInfoSiteMapper.updateMercadoShopInfoSite(mercadoShopInfoSite);
    }

    /**
     * 批量删除店铺站点信息
     *
     * @param ids 需要删除的店铺站点信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoShopInfoSiteByIds(Long[] ids) {
        return mercadoShopInfoSiteMapper.deleteMercadoShopInfoSiteByIds(ids);
    }

    /**
     * 删除店铺站点信息信息
     *
     * @param id 店铺站点信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoShopInfoSiteById(Long id) {
        return mercadoShopInfoSiteMapper.deleteMercadoShopInfoSiteById(id);
    }
}
