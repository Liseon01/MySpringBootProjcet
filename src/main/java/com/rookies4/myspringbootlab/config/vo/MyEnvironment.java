package com.rookies4.myspringbootlab.config.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString

public class MyEnvironment {
    private String mode;
}
