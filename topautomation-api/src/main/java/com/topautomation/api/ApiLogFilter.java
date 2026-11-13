package com.topautomation.api;

import com.aventstack.extentreports.ExtentTest;
import com.topautomation.core.reporting.ExtentTestManager;
import com.topautomation.core.utils.HtmlUtils;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Logs every call (method, URL, status, time, bodies) to log4j and to the current test in the Extent report. */
public class ApiLogFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger(ApiLogFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification response,
                           FilterContext context) {
        long start = System.nanoTime();
        Response result = context.next(request, response);
        long ms = (System.nanoTime() - start) / 1_000_000;

        String summary = request.getMethod() + " " + request.getURI() + " -> " + result.getStatusCode() + " (" + ms + " ms)";
        LOG.info(summary);

        ExtentTest test = ExtentTestManager.get();
        if (test != null) {
            StringBuilder sb = new StringBuilder("<b>").append(HtmlUtils.escape(summary)).append("</b>");
            Object body = request.getBody();
            if (body instanceof String s && !s.isBlank()) {
                sb.append("<br/>Request body:<pre>").append(HtmlUtils.escape(mask(HtmlUtils.truncate(s, 1000)))).append("</pre>");
            }
            sb.append("Response body:<pre>").append(HtmlUtils.escape(HtmlUtils.truncate(result.asString(), 1000))).append("</pre>");
            test.info(sb.toString());
        }
        return result;
    }

    private static String mask(String s) {
        return s.replaceAll("(?i)(\"password\"\\s*:\\s*\")[^\"]*\"", "$1***\"");
    }
}
