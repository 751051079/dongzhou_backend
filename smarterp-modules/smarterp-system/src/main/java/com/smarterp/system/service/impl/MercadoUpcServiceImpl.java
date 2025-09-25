package com.smarterp.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.smarterp.common.core.web.domain.AjaxResult;
import com.smarterp.common.datascope.annotation.DataScope;
import com.smarterp.common.security.utils.SecurityUtils;
import com.smarterp.system.api.domain.SysUser;
import com.smarterp.system.domain.MercadoUpc;
import com.smarterp.system.domain.MercadoUpcItem;
import com.smarterp.system.domain.dto.MercadoUpcDTO;
import com.smarterp.system.domain.vo.MercadoUpcVO;
import com.smarterp.system.mapper.MercadoUpcItemMapper;
import com.smarterp.system.mapper.MercadoUpcMapper;
import com.smarterp.system.mapper.SysUserMapper;
import com.smarterp.system.service.IMercadoUpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * UPC信息Service业务层处理
 *
 * @author smarterp
 * @date 2023-04-16
 */
@Service
public class MercadoUpcServiceImpl implements IMercadoUpcService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoUpcServiceImpl.class);

    @Resource
    private MercadoUpcMapper mercadoUpcMapper;

    @Resource
    private MercadoUpcItemMapper mercadoUpcItemMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 查询UPC信息
     *
     * @param id UPC信息主键
     * @return UPC信息
     */
    @Override
    public MercadoUpc selectMercadoUpcById(Long id) {
        return mercadoUpcMapper.selectMercadoUpcById(id);
    }

    /**
     * 查询UPC信息列表
     *
     * @param mercadoUpc UPC信息
     * @return UPC信息
     */
    @Override
    @DataScope(deptAlias = "dept",userAlias = "su")
    public List<MercadoUpcVO> selectMercadoUpcList(MercadoUpc mercadoUpc) {
        return mercadoUpcMapper.selectMercadoUpcDeptList(mercadoUpc);
    }

    /**
     * 新增UPC信息
     *
     * @param mercadoUpcDTO UPC信息
     * @return 结果
     */
    @Override
    public AjaxResult insertMercadoUpc(MercadoUpcDTO mercadoUpcDTO) {
        // 首先获取到当前登录的用户信息
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return AjaxResult.error("当前用户登录已失效，请重新登录");
        }
        // 根据用户id查询用户详细信息
        SysUser currentUser = sysUserMapper.selectUserById(userId);
        if (currentUser == null) {
            return AjaxResult.error("当前用户登录已失效，请重新登录");
        }
        // 号码池关联部门deptId，根据deptId和号码池名称查询是否有重复的号码池
        MercadoUpc example = new MercadoUpc();
        example.setDeptId(currentUser.getDeptId());
        example.setCodeName(mercadoUpcDTO.getCodeName());
        example.setDeleted(false);
        List<MercadoUpc> mercadoUpcList = mercadoUpcMapper.selectMercadoUpcList(example);
        if (!mercadoUpcList.isEmpty()) {
            return AjaxResult.error("存在相同名称的号码池，请重新上传UPC");
        }
        String[] split = mercadoUpcDTO.getUpcContent().split("\\r?\\n");
        if (split.length > 0) {
            // 插入号码池信息
            MercadoUpc mercadoUpc = new MercadoUpc();
            mercadoUpc.setId(IdUtil.getSnowflake(1, 1).nextId());
            mercadoUpc.setDeptId(currentUser.getDeptId());
            mercadoUpc.setCodeName(mercadoUpcDTO.getCodeName());
            mercadoUpc.setCodeType("UPC");
            mercadoUpc.setTotalNum(Long.parseLong(String.valueOf(split.length)));
            mercadoUpc.setAvailableNum(Long.parseLong(String.valueOf(split.length)));
            mercadoUpc.setUsedNum(0L);
            mercadoUpc.setCreateTime(new Date());
            mercadoUpc.setCreateBy(String.valueOf(currentUser.getUserId()));
            mercadoUpc.setUpdateTime(new Date());
            mercadoUpc.setUpdateBy(String.valueOf(currentUser.getUserId()));
            mercadoUpc.setDeleted(false);
            logger.info("插入的UPC数据：" + JSON.toJSONString(mercadoUpc));
            Integer integer = mercadoUpcMapper.insertMercadoUpc(mercadoUpc);
            if (integer > 0) {
                for (int index = 0; index < split.length; index++) {
                    String upcCode = split[index];
                    MercadoUpcItem mercadoUpcItem = new MercadoUpcItem();
                    mercadoUpcItem.setId(IdUtil.getSnowflake(1, 1).nextId());
                    mercadoUpcItem.setUpcId(mercadoUpc.getId());
                    mercadoUpcItem.setUpcCode(upcCode.trim());
                    mercadoUpcItem.setUpcStatus("NO");
                    mercadoUpcItem.setCreateTime(new Date());
                    mercadoUpcItem.setCreateBy(String.valueOf(currentUser.getUserId()));
                    mercadoUpcItem.setUpdateTime(new Date());
                    mercadoUpcItem.setUpdateBy(String.valueOf(currentUser.getUserId()));
                    mercadoUpcItem.setDeleted(false);
                    logger.info("插入的UPC详情数据：" + JSON.toJSONString(mercadoUpcItem));
                    mercadoUpcItemMapper.insertMercadoUpcItem(mercadoUpcItem);
                }
                return AjaxResult.success("上传UPC成功");
            }
        }
        return AjaxResult.error("上传UPC失败");
    }

    /**
     * 修改UPC信息
     *
     * @param mercadoUpc UPC信息
     * @return 结果
     */
    @Override
    public int updateMercadoUpc(MercadoUpc mercadoUpc) {
        return mercadoUpcMapper.updateMercadoUpc(mercadoUpc);
    }

    /**
     * 批量删除UPC信息
     *
     * @param ids 需要删除的UPC信息主键
     * @return 结果
     */
    @Override
    public int deleteMercadoUpcByIds(Long[] ids) {
        return mercadoUpcMapper.deleteMercadoUpcByIds(ids);
    }

    /**
     * 删除UPC信息信息
     *
     * @param id UPC信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteMercadoUpcById(Long id) {
        // 根据upcId删除upc详情
        Integer result = mercadoUpcItemMapper.deleteMercadoUpcItemByUpcId(id);
        return mercadoUpcMapper.deleteMercadoUpcById(id);
    }

    @Override
    public List<MercadoUpcItem> selectMercadoUpcItemList(MercadoUpcItem mercadoUpcItem) {
        return mercadoUpcItemMapper.selectMercadoUpcItemList(mercadoUpcItem);
    }
}
