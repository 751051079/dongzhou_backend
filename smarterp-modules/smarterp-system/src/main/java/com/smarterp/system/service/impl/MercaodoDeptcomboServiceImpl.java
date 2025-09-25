package com.smarterp.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.system.domain.MercaodoDeptcombo;
import com.smarterp.system.mapper.MercaodoDeptcomboMapper;
import com.smarterp.system.service.IMercaodoDeptcomboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 部门与套餐关联Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-20
 */
@Service
public class MercaodoDeptcomboServiceImpl implements IMercaodoDeptcomboService {

    private static final Logger logger = LoggerFactory.getLogger(MercaodoDeptcomboServiceImpl.class);

    @Resource
    private MercaodoDeptcomboMapper mercaodoDeptcomboMapper;

    /**
     * 查询部门与套餐关联
     *
     * @param id 部门与套餐关联主键
     * @return 部门与套餐关联
     */
    @Override
    public MercaodoDeptcombo selectMercaodoDeptcomboById(Long id) {
        return mercaodoDeptcomboMapper.selectMercaodoDeptcomboById(id);
    }

    /**
     * 查询部门与套餐关联列表
     *
     * @param mercaodoDeptcombo 部门与套餐关联
     * @return 部门与套餐关联
     */
    @Override
    public List<MercaodoDeptcombo> selectMercaodoDeptcomboList(MercaodoDeptcombo mercaodoDeptcombo) {
        return mercaodoDeptcomboMapper.selectMercaodoDeptcomboList(mercaodoDeptcombo);
    }

    /**
     * 新增部门与套餐关联
     *
     * @param mercadoDeptcomboQuery 部门与套餐关联
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult insertMercaodoDeptcombo(MercaodoDeptcombo mercadoDeptcomboQuery) {
        // 首先根据部门id查询是否已经绑定过套餐
        MercaodoDeptcombo example = new MercaodoDeptcombo();
        example.setDeptId(mercadoDeptcomboQuery.getDeptId());
        List<MercaodoDeptcombo> deptcomboList = mercaodoDeptcomboMapper.selectMercaodoDeptcomboList(example);
        if (!deptcomboList.isEmpty()) {
            // 将之前绑定的套餐删除掉
            for (MercaodoDeptcombo item : deptcomboList) {
                mercaodoDeptcomboMapper.deleteMercaodoDeptcomboById(item.getId());
            }
        }
        MercaodoDeptcombo mercaodoDeptcombo = new MercaodoDeptcombo();
        mercaodoDeptcombo.setId(IdUtil.getSnowflake(1, 1).nextId());
        mercaodoDeptcombo.setDeptId(mercadoDeptcomboQuery.getDeptId());
        mercaodoDeptcombo.setComboId(mercadoDeptcomboQuery.getComboId());
        mercaodoDeptcombo.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        mercaodoDeptcombo.setCreateTime(new Date());
        mercaodoDeptcombo.setUpdateBy(String.valueOf(SecurityUtils.getUserId()));
        mercaodoDeptcombo.setUpdateTime(new Date());
        mercaodoDeptcombo.setDeleted(false);
        int result = mercaodoDeptcomboMapper.insertMercaodoDeptcombo(mercaodoDeptcombo);
        return result > 0 ? AjaxResult.success("绑定套餐成功") : AjaxResult.error("绑定套餐失败");
    }

    /**
     * 修改部门与套餐关联
     *
     * @param mercaodoDeptcombo 部门与套餐关联
     * @return 结果
     */
    @Override
    public int updateMercaodoDeptcombo(MercaodoDeptcombo mercaodoDeptcombo) {
        return mercaodoDeptcomboMapper.updateMercaodoDeptcombo(mercaodoDeptcombo);
    }

    /**
     * 批量删除部门与套餐关联
     *
     * @param ids 需要删除的部门与套餐关联主键
     * @return 结果
     */
    @Override
    public int deleteMercaodoDeptcomboByIds(Long[] ids) {
        return mercaodoDeptcomboMapper.deleteMercaodoDeptcomboByIds(ids);
    }

    /**
     * 删除部门与套餐关联信息
     *
     * @param id 部门与套餐关联主键
     * @return 结果
     */
    @Override
    public int deleteMercaodoDeptcomboById(Long id) {
        return mercaodoDeptcomboMapper.deleteMercaodoDeptcomboById(id);
    }
}
