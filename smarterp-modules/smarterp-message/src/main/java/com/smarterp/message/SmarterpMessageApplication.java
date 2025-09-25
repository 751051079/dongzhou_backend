package com.smarterp.message;

import com.smarterp.common.security.annotation.EnableCustomConfig;
import com.smarterp.common.security.annotation.EnableRyFeignClients;
import com.smarterp.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 系统模块
 *
 * @author smarterp
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
@EnableScheduling
public class SmarterpMessageApplication {
    public static void main(String[] args) {

        SpringApplication.run(SmarterpMessageApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  ERP消息模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }

}
