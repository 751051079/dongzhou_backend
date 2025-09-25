package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoProduct;
import com.smarterp.order.domain.dto.MercadoShopInfo;
import com.smarterp.order.domain.dto.MercadoShopInfoSite;
import com.smarterp.order.domain.vo.MercadoProductDTO;
import com.smarterp.order.domain.vo.MercadoProductDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 产品Mapper接口
 *
 * @author smarterp
 * @date 2023-04-26
 */
public interface MercadoProductMapper {
    /**
     * 查询产品
     *
     * @param id 产品主键
     * @return 产品
     */
    public MercadoProduct selectMercadoProductById(Long id);

    /**
     * 查询产品列表
     *
     * @param mercadoProduct 产品
     * @return 产品集合
     */
    public List<MercadoProductDetail> selectMercadoProductList(MercadoProduct mercadoProduct);

    /**
     * 新增产品
     *
     * @param mercadoProduct 产品
     * @return 结果
     */
    public int insertMercadoProduct(MercadoProduct mercadoProduct);

    /**
     * 修改产品
     *
     * @param mercadoProduct 产品
     * @return 结果
     */
    public int updateMercadoProduct(MercadoProduct mercadoProduct);

    /**
     * 删除产品
     *
     * @param id 产品主键
     * @return 结果
     */
    public int deleteMercadoProductById(Long id);

    /**
     * 批量删除产品
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoProductByIds(Long[] ids);

    /**
     * 分页获取店铺信息
     *
     * @param index
     * @param pageSize
     * @return
     */
    public List<MercadoShopInfo> getShopList(@Param("index") int index, @Param("pageSize") int pageSize);

    /**
     * 更新token信息
     *
     * @param mercadoShopInfo
     */
    public void updateMercadoToken(MercadoShopInfo mercadoShopInfo);

    /**
     * 根据用户部门id查询店铺信息
     *
     * @param deptId
     * @return
     */
    public MercadoShopInfo getMercadoToken(Long deptId);

    /**
     * 根据部门id，商家id和全局产品id查询是否有重复采集的数据
     *
     * @param mercadoProductDTO
     * @return
     */
    Integer selectCountBy(MercadoProductDTO mercadoProductDTO);

    /**
     * 根据商家id查询accessToken
     *
     * @param merchantId
     * @return
     */
    MercadoShopInfo getAccessTokenByMerchantId(Long merchantId);

    Map<String, Object> getOneUpcByDeptId(Long deptId);

    void updateUpcItemStatus(Long id);

    void updateUpcCount(Long upcId);

    List<MercadoProduct> pullProductIdLyh(@Param("page") int page, @Param("size") int size);

    long selectProductLyhById(Long id);

    void addProductLyh(MercadoProduct pr);

    List<MercadoProduct> pullProductByLyh(@Param("page") int page, @Param("size") int size);

    List<Long> selectProductLyhByTitle(String productTitle);

    long selectProductLyhByTitle102(String productTitle);

    void deleteProductLyhById(List<Long> ids);

    /**
     * 批量删除
     *
     * @param result
     * @return
     */
    Integer deleteMercadoProductByProductIds(List<Long> result);

    MercadoShopInfo getAccessTokenByMerchantIds(Long merchantId);

    /**
     * 根据产品id将产品状态更改为发布中
     *
     * @param productIds
     * @return
     */
    Integer updateMercadoProductStatus(@Param("productIds") String[] productIds);

    /**
     * 根据站点id查询站点信息
     *
     * @param userId
     * @return
     */
    MercadoShopInfoSite getMercadoInfoSite(String userId);

    /**
     * 获取所有店铺详情
     *
     * @return
     */
    List<MercadoShopInfoSite> getMercadoInfoSiteAll();

    /**
     * 根据商家id获取所有店铺站点信息
     * @param merchantId
     * @return
     */
    List<MercadoShopInfoSite> getMercadoInfoSiteByMerchantId(String merchantId);
}
