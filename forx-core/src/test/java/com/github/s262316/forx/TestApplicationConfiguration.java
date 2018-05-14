package com.github.s262316.forx;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.github.s262316.forx.client.Launcher;

@Configuration@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {CommandLineRunner.class, Launcher.class, ForxApplication.class}))
public class TestApplicationConfiguration
{
}
