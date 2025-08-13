package com.rookies4.myspringbootlab.runner;

import com.rookies4.myspringbootlab.config.vo.MyEnvironment;
import com.rookies4.myspringbootlab.property.MyPropProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
public class MyPropsRunner implements ApplicationRunner {
    @Value("${myprop.name}")
    private String username;

    @Value("${myprop.port}")
    private int port;

    @Autowired
    private MyPropProperties properties;

    @Autowired
    private MyEnvironment environment;

    private Logger logger = LoggerFactory.getLogger(MyPropsRunner.class);

    public void run(ApplicationArguments args) throws Exception {
        logger.debug("로거 구현객체명 = {}", logger.getClass().getName());
        log.info("myprop.name = {}", username );
        log.info("myprop.port = {}", port );

        log.info("MyBootProperties.getName() = "+ properties.getName());
        log.info("MyBootProperties.getPort() = "+ properties.getPort());

        log.info("현재 활성화된 MyEnvironment = " + environment);

    }
}
