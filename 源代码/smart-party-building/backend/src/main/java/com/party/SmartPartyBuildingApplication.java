package com.party;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.party.mapper")
public class SmartPartyBuildingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPartyBuildingApplication.class, args);
    }
}
