package com.rookies4.myspringbootlab.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter@Setter
@Component
@ConfigurationProperties("myprop")
public class MyPropProperties {
    private String name;
    private int port;
}
