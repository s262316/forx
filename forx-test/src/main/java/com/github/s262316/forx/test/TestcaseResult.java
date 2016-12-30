package com.github.s262316.forx.test;

public class TestcaseResult
{
    private String testcaseName;
    private String expectedScreenshotUrl;
    private String actualScreenshotUrl;
    private String result;

    public TestcaseResult(String actualScreenshotUrl, String expectedScreenshotUrl, String testcaseName, String result)
    {
        this.actualScreenshotUrl = actualScreenshotUrl;
        this.expectedScreenshotUrl = expectedScreenshotUrl;
        this.testcaseName = testcaseName;
        this.result=result;
    }

    public String getActualScreenshotUrl()
    {
        return actualScreenshotUrl;
    }

    public String getExpectedScreenshotUrl()
    {
        return expectedScreenshotUrl;
    }

    public String getTestcaseName()
    {
        return testcaseName;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }
}
