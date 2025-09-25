package com.smarterp.system.mapper;

import java.util.List;

import com.smarterp.system.domain.MercadoShopInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 店铺Mapper接口
 *
 * @author smarterp
 * @date 2023-04-19
 */
public interface MercadoShopInfoMapper {
    /**
     * 查询店铺
     *
     * @param id 店铺主键
     * @return 店铺
     */
    public MercadoShopInfo selectMercadoShopInfoById(Long id);

    /**
     * 查询店铺列表
     *
     * @param mercadoShopInfo 店铺
     * @return 店铺集合
     */
    public List<MercadoShopInfo> selectMercadoShopInfoList(MercadoShopInfo mercadoShopInfo);

    /**
     * 新增店铺
     *
     * @param mercadoShopInfo 店铺
     * @return 结果
     */
    public int insertMercadoShopInfo(MercadoShopInfo mercadoShopInfo);

    /**
     * 修改店铺
     *
     * @param mercadoShopInfo 店铺
     * @return 结果
     */
    public int updateMercadoShopInfo(MercadoShopInfo mercadoShopInfo);

    /**
     * 删除店铺
     *
     * @param id 店铺主键
     * @return 结果
     */
    public int deleteMercadoShopInfoById(Long id);

    /**
     * 批量删除店铺
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoShopInfoByIds(Long[] ids);

    /**
     * 根据商家id查询店铺信息
     *
     * @param merchantId
     * @return
     */
    public MercadoShopInfo selectMercadoShopByMerchantId(String merchantId);

    /**
     * 根据商家id去查询旧的店铺表，如果能查到就用之前的旧的主键id
     *
     * @param merchantId
     * @return
     */
    public Long selectIdByMerchantId(String merchantId);

    /**
     * 分页获取店铺信息
     *
     * @param index
     * @param pageSize
     * @return
     */
    public List<MercadoShopInfo> getShopList(@Param("index") int index, @Param("pageSize") int pageSize);

    /**
     * 更新所有店铺的token
     *
     * @param mercadoShopInfo
     */
    public void updateMercadoToken(MercadoShopInfo mercadoShopInfo);

    /**
     * 查询所有的店铺信息
     * @return
     */
    public List<MercadoShopInfo> selectAllList();
}
