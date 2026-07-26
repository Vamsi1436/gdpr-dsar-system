package com.dsar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
  @EnableScheduling
  public class DsarApplication {

public static void main(String[] args) {
  SpringApplication.run(DsarApplication.class, args);
}

  }
