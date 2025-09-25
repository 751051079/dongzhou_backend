package com.smarterp.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smarterp.system.mapper.MercadoUpcItemMapper;
import com.smarterp.system.domain.MercadoUpcItem;
import com.smarterp.system.service.IMercadoUpcItemService;

/**
 * UPC信息详情Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-16
 */
@Service
public class MercadoUpcItemServiceImpl implements IMercadoUpcItemService {

    @Autowired
    private MercadoUpcItemMapper mercadoUpcItemMapper;

    /**
     * 查询UPC信息详情
     *
     * @param id UPC信息详情主键
     * @return UPC信息详情
     */
    @Override
    public MercadoUpcItem selectMercadoUpcItemById(Long id) {
        return mercadoUpcItemMapper.selectMercadoUpcItemById(id);
    }

    /**
     * 查询UPC信息详情列表
     *
     * @param mercadoUpcItem UPC信息详情
     * @return UPC信息详情
     */
    @Override
    public List<MercadoUpcItem> selectMercadoUpcItemList(MercadoUpcItem mercadoUpcItem) {
        return mercadoUpcItemMapper.selectMercadoUpcItemList(mercadoUpcItem);
    }

    /**
     * 新增UPC信息详情
     *
     * @param mercadoUpcItem UPC信息详情
     * @return 结果
     */
    @Override
    public int insertMercadoUpcItem(MercadoUpcItem mercadoUpcItem) {
        return mercadoUpcItemMapper.insertMercadoUpcItem(mercadoUpcItem);
    }

    /**
     * 修改UPC信息详情
     *
     * @param mercadoUpcItem UPC信息详情
     * @return 结果
     */
    @Override
    public int updateMercadoUpcItem(MercadoUpcItem mercadoUpcItem) {
        return mercadoUpcItemMapper.updateMercadoUpcItem(mercadoUpcItem);
    }

    /**
     * 批量删除UPC信息详情
     *
     * @param ids 需要删除的UPC信息详情主键
     * @return 结果
     */
    @Override
    public int deleteMercadoUpcItemByIds(Long[] ids) {
        return mercadoUpcItemMapper.deleteMercadoUpcItemByIds(ids);
    }

    /**
     * 删除UPC信息详情信息
     *
     * @param id UPC信息详情主键
     * @return 结果
     */
    @Override
    public int deleteMercadoUpcItemById(Long id) {
        return mercadoUpcItemMapper.deleteMercadoUpcItemById(id);
    }
}
