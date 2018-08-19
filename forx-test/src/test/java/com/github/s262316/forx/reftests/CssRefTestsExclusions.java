package com.github.s262316.forx.reftests;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@Configuration
@ConfigurationProperties("exclusions")
public class CssRefTestsExclusions
{
	private List<Path> files;

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
}
