package com.smarterp.auth.domain.vo;

import java.io.Serializable;

/**
 * 部门表 sys_dept
 *
 * @author smarterp
 */
public class SysDeptVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 套餐剩余有效天数
     */
    private Integer comboEfficientDays;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getComboEfficientDays() {
        return comboEfficientDays;
    }

    public void setComboEfficientDays(Integer comboEfficientDays) {
        this.comboEfficientDays = comboEfficientDays;
    }
}
