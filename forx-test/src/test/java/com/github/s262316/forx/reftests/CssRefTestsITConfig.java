package com.github.s262316.forx.reftests;

import com.github.s262316.forx.ForxApplication;
import com.github.s262316.forx.client.Launcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.github.s262316.forx",
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { CommandLineRunner.class, Launcher.class,
			ForxApplication.class }))
public class CssRefTestsITConfig
{
}
