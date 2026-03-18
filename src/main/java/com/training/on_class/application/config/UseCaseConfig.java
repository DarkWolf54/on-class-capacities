package com.training.on_class.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
  basePackages = "com.training.on_class.domain.usecase",
  includeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
  },
  useDefaultFilters = false
)
public class UseCaseConfig {
}