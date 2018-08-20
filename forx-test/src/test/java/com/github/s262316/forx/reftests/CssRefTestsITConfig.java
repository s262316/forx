package com.github.s262316.forx.reftests;

import com.github.s262316.forx.ForxApplication;
import com.github.s262316.forx.client.Launcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@ComponentScan(basePackages = "com.github.s262316.forx",
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { CommandLineRunner.class, Launcher.class,
			ForxApplication.class }))
public class CssRefTestsITConfig
{
	@Value("${app.version}")
	private String version;
	private Path actualResultsFolder;

	@Value("${actualResultsFolder}")
	public void setActualResultsFolder(Path actualResultsFolder)
	{
		this.actualResultsFolder=actualResultsFolder.resolve(
				version+"_"+ DateTimeFormatter.ofPattern("yyyyMMdd_HHmm").format(LocalDateTime.now()));
	}

	public Path getActualResultsFolder()
	{
		return actualResultsFolder;
	}
}
