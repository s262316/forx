package com.github.s262316.forx.test.controller;

import com.google.common.base.MoreObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebAppActualResultsLocation implements ActualResultsLocation
{
	@Autowired
	HttpServletRequest request;
	@Value("${baseTestResultsFolder}")
	private File baseTestResultsFolder;

	@Override
	public Path getActualResultsFolder()
	{
		String folderParam= MoreObjects.firstNonNull(request.getParameter("folder"), "");
		return baseTestResultsFolder.toPath().resolve(Paths.get(folderParam).getName(0));
	}
}
