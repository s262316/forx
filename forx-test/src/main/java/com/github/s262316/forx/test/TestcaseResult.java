package com.github.s262316.forx.test;

public class TestcaseResult
{
    private String testcaseName;
    private String firefoxScreenshotUrl;
    private String refScreenshotUrl;
    private String actualScreenshotUrl;
    private String result;
    private final boolean auto;
    private final boolean autoIgnored;
    private final boolean manual;

    public TestcaseResult(String actualScreenshotUrl, String refScreenshotUrl,
            String firefoxScreenshotUrl, String testcaseName,
            String result,
            boolean auto, boolean autoIgnored, boolean manual)
    {
        this.actualScreenshotUrl = actualScreenshotUrl;
        this.refScreenshotUrl=refScreenshotUrl;
        this.firefoxScreenshotUrl = firefoxScreenshotUrl;
        this.testcaseName = testcaseName;
        this.result=result;
        this.auto = auto;
        this.autoIgnored = autoIgnored;
        this.manual = manual;
    }

    public String getFirefoxScreenshotUrl()
    {
        return firefoxScreenshotUrl;
    }

    public String getActualScreenshotUrl()
    {
        return actualScreenshotUrl;
    }

    public String getRefScreenshotUrl()
    {
        return refScreenshotUrl;
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

    public boolean isAuto()
    {
        return auto;
    }

    public boolean isAutoIgnored()
    {
        return autoIgnored;
    }

    public boolean isManual()
    {
        return manual;
    }
}
