package com.github.s262316.forx.test.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@ConfigurationProperties("exclusions")
public class CssRefTestsExclusions
{
	@Value("${applicationPropertiesSourceLocation}")
	private File applicationProperties;

	private List<Path> files=new ArrayList<>();

	public List<Path> getFiles()
	{
		return files;
	}

	public void setFiles(List<Path> files)
	{
		this.files = files;
	}

	public boolean isExcluded(Path cssTestSuiteFolder, Path p)
	{
		return files.stream()
				.map(v -> cssTestSuiteFolder.resolve(v))
				.anyMatch(v -> v.equals(p));
	}

	public void addExclusion(String testcaseName) throws IOException
	{
		Properties p=new Properties();
		p.load(new FileInputStream(applicationProperties));

		int nextIndex=p.stringPropertyNames().stream()
				.filter(v -> v.startsWith("exclusions.files["))
				.map(v -> StringUtils.removeStart(v, "exclusions.files["))
				.map(v -> StringUtils.removeEnd(v, "]"))
				.mapToInt(Integer::parseInt)
				.max()
				.orElse(-1)
				+1;

		p.put("exclusions.files["+nextIndex+"]", testcaseName);

		p.store(new FileOutputStream(applicationProperties), "");

	}
}
