package com.smarterp.system.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.IdUtil;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.system.api.domain.SysUser;
import com.smarterp.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smarterp.system.mapper.MercadoComboInfoMapper;
import com.smarterp.system.domain.MercadoComboInfo;
import com.smarterp.system.service.IMercadoComboInfoService;

/**
 * 套餐Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-16
 */
@Service
public class MercadoComboInfoServiceImpl implements IMercadoComboInfoService {

    @Autowired
    private MercadoComboInfoMapper mercadoComboInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询套餐
     *
     * @param id 套餐主键
     * @return 套餐
     */
    @Override
    public MercadoComboInfo selectMercadoComboInfoById(Long id) {
        return mercadoComboInfoMapper.selectMercadoComboInfoById(id);
    }

    /**
     * 查询套餐列表
     *
     * @param mercadoComboInfo 套餐
     * @return 套餐
     */
    @Override
    public List<MercadoComboInfo> selectMercadoComboInfoList(MercadoComboInfo mercadoComboInfo) {
        return mercadoComboInfoMapper.selectMercadoComboInfoList(mercadoComboInfo);
    }

    /**
     * 新增套餐
     *
     * @param mercadoComboInfo 套餐
     * @return 结果
     */
    @Override
    public int insertMercadoComboInfo(MercadoComboInfo mercadoComboInfo) {
        // 获取当前用户信息
        mercadoComboInfo.setId(IdUtil.getSnowflake(1, 1).nextId());
        mercadoComboInfo.setCreateTime(new Date());
        mercadoComboInfo.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        mercadoComboInfo.setUpdateTime(new Date());
        mercadoComboInfo.setUpdateBy(String.valueOf(SecurityUtils.getUserId()));
        mercadoComboInfo.setDeleted(false);
        return mercadoComboInfoMapper.insertMercadoComboInfo(mercadoComboInfo);
    }

    /**
     * 修改套餐
     *
     * @param mercadoComboInfo 套餐
     * @return 结果
     */
    @Override
    public int updateMercadoComboInfo(MercadoComboInfo mercadoComboInfo) {
        mercadoComboInfo.setUpdateBy(String.valueOf(SecurityUtils.getUserId()));
        mercadoComboInfo.setUpdateTime(new Date());
        return mercadoComboInfoMapper.updateMercadoComboInfo(mercadoComboInfo);
    }

    /**
     * 批量删除套餐
     *
     * @param ids 需要删除的套餐主键
     * @return 结果
     */
    @Override
    public int deleteMercadoComboInfoByIds(Long[] ids) {
        return mercadoComboInfoMapper.deleteMercadoComboInfoByIds(ids);
    }

    /**
     * 删除套餐信息
     *
     * @param id 套餐主键
     * @return 结果
     */
    @Override
    public int deleteMercadoComboInfoById(Long id) {
        return mercadoComboInfoMapper.deleteMercadoComboInfoById(id);
    }
}
