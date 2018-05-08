package com.github.s262316.forx.test.controller;

import com.google.common.collect.Iterators;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TestcaseController
{
    private static Logger logger= LoggerFactory.getLogger(TestcaseController.class);

    @Value("${cssTestSuiteFolder}")
    private Path cssTestSuiteFolder;

    @RequestMapping("/testcases/**")
    public ResponseEntity<byte[]> testcase(HttpServletRequest request) throws IOException
    {
        String fullPath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String path = apm.extractPathWithinPattern(bestMatchPattern, fullPath);

        HttpHeaders defaultHeaders=new HttpHeaders();

        if(path.endsWith(".xht"))
            defaultHeaders.setContentType(new MediaType("application", "xhtml+xml"));
        else if(path.endsWith(".com.github.s262316.forx.css"))
            defaultHeaders.setContentType(new MediaType("text", "css"));
        else
            logger.error("unexpected file extension");

        Map<String, String> overridenHeaders=new HashMap<>();

        Path file=cssTestSuiteFolder.resolve(path);

        byte[] filedata= IOUtils.toByteArray(Files.newInputStream(file, StandardOpenOption.READ));

        // is there a .headers file
        String lastPathPart=Iterators.getLast(file.iterator()).toString();

        Path headersFile=file.resolve("../"+lastPathPart+".headers");
        if(Files.exists(headersFile))
        {
            overridenHeaders=Files.lines(headersFile)
                    .collect(Collectors.toMap(
                    v -> StringUtils.substringBefore(v, ":"),
                    v -> StringUtils.substringAfter(v, ":")
            ));
        }

        defaultHeaders.setAll(overridenHeaders);

        ResponseEntity<byte[]> responseEntity=new ResponseEntity<>(filedata, defaultHeaders, HttpStatus.OK);

        return responseEntity;
    }
}
