package com.rookies4.myspringbootlab.config;

import com.rookies4.myspringbootlab.config.vo.MyEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class ProdConfig {
    @Bean
    public MyEnvironment MyEnvironment(){
        return MyEnvironment.builder().mode("운영환경").build();
    }
}
