package com.smarterp.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smarterp.common.core.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * UPC信息对象 mercado_upc
 *
 * @author smarterp
 * @date 2023-04-16
 */
public class MercadoUpcVO implements Serializable {

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
     * 部门name
     */
    @Excel(name = "部门name")
    private String deptName;


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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;

    private String createName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public Long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Long getAvailableNum() {
        return availableNum;
    }

    public void setAvailableNum(Long availableNum) {
        this.availableNum = availableNum;
    }

    public Long getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Long usedNum) {
        this.usedNum = usedNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }
}
