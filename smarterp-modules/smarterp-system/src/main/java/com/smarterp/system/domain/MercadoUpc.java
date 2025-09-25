package com.smarterp.system.domain;

import com.smarterp.common.core.annotation.Excel;
import com.smarterp.common.core.web.domain.BaseEntity;

/**
 * UPC信息对象 mercado_upc
 *
 * @author smarterp
 * @date 2023-04-16
 */
public class MercadoUpc extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 部门id
     */
    @Excel(name = "部门id")
    private Long deptId;

    /**
     * 号码池名称
     */
    @Excel(name = "号码池名称")
    private String codeName;

    /**
     * 号码池类型
     */
    @Excel(name = "号码池类型")
    private String codeType;

    /**
     * 总数
     */
    @Excel(name = "总数")
    private Long totalNum;

    /**
     * 可用
     */
    @Excel(name = "可用")
    private Long availableNum;

    /**
     * 已使用
     */
    @Excel(name = "已使用")
    private Long usedNum;

    /**
     * 是否删除标识，0表示未删除，1表示已删除
     */
    @Excel(name = "是否删除标识，0表示未删除，1表示已删除")
    private Boolean isDeleted;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Long getTotalNum() {
        return totalNum;
    }

    public void setAvailableNum(Long availableNum) {
        this.availableNum = availableNum;
    }

    public Long getAvailableNum() {
        return availableNum;
    }

    public void setUsedNum(Long usedNum) {
        this.usedNum = usedNum;
    }

    public Long getUsedNum() {
        return usedNum;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
