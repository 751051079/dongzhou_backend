package com.smarterp.order.mapper;

import com.smarterp.order.domain.MercadoCallBack;
import com.smarterp.order.domain.MercadoProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 美客多回调信息Mapper接口
 *
 * @author smarterp
 * @date 2023-05-30
 */
public interface MercadoCallBackMapper {
    /**
     * 查询美客多回调信息
     *
     * @param id 美客多回调信息主键
     * @return 美客多回调信息
     */
    public MercadoCallBack selectMercadoCallBackById(Long id);

    /**
     * 查询美客多回调信息列表
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 美客多回调信息集合
     */
    public List<MercadoCallBack> selectMercadoCallBackList(MercadoCallBack mercadoCallBack);

    /**
     * 新增美客多回调信息
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 结果
     */
    public int insertMercadoCallBack(MercadoCallBack mercadoCallBack);

    /**
     * 修改美客多回调信息
     *
     * @param mercadoCallBack 美客多回调信息
     * @return 结果
     */
    public int updateMercadoCallBack(MercadoCallBack mercadoCallBack);

    /**
     * 删除美客多回调信息
     *
     * @param id 美客多回调信息主键
     * @return 结果
     */
    public int deleteMercadoCallBackById(Long id);

    /**
     * 批量删除美客多回调信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMercadoCallBackByIds(Long[] ids);

    /**
     * 查询需要处理的产品id
     * @param limit 限制数量
     * @return
     */
    public List<Long> getProductIds(Integer limit);

    /**
     * 批量更新产品的状态
     * @param productIds
     * @return
     */
    Integer updateStatusByProductIds(List<Long> productIds);

    /**
     * 查询产品表中未发布状态的30个产品id
     * @return
     */
    List<Long> pullProductLijuntao();


    /**
     * 查询需要处理的产品id

     * @return
     */
    public List<Long> getProductIdsByLyh(@Param("page") int page,@Param("size") int size);

    /**
     * 批量更新产品的状态
     * @param productIds
     * @return
     */
    Integer updateStatusByProductIdsByLyh(List<Long> productIds);

    /**
     * 查询产品表中未发布状态的30个产品id
     * @return
     */
    Long pullProductLyh();


    List<Long> deleteProductLijuntao();

    List<Long> selectProductIdsByLimit(@Param("limit") Integer limit);

    Integer backUpProductData(List<Long> result);

    Integer backUpProductItemData(List<Long> result);

    Integer backUpProductAttributesData(List<Long> result);

    Integer backUpProductCombinationData(List<Long> result);
}
