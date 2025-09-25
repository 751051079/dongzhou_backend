package com.smarterp.job.task;

import com.smarterp.common.core.utils.StringUtils;
import com.smarterp.system.api.RemoteSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务调度测试
 *
 * @author smarterp
 */
@Component("ryTask")
public class RyTask {

    private static final Logger log = LoggerFactory.getLogger(RyTask.class);

    @Resource
    private RemoteSystemService remoteOrderService;

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams() {
        System.out.println("执行无参方法");
    }

    /**
     * 更新token
     */
    public void updateMercadoToken() {
        log.info("更新token任务开始");
        remoteOrderService.updateMercadoToken();
        log.info("更新token任务结束");
    }

    /**
     * 更新部门绑定套餐剩余有效天数定时任务
     */
    public void updateComboValidDate() {
        log.info("更新部门绑定套餐剩余有效天数定时任务开始");
        remoteOrderService.updateComboValidDate();
        log.info("更新部门绑定套餐剩余有效天数定时任务结束");
    }
}
