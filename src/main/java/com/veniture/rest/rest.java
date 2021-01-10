package com.veniture.rest;


import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//imports for currency exchanger

@Path("/main")
public class rest {
    @JiraImport
    private RequestFactory requestFactory;
    //private ApplicationProperties applicationProperties;
    @JiraImport
    private SearchService searchService;
    @JiraImport
    private JiraAuthenticationContext authenticationContext;
    @JiraImport
    private IssueManager issueManager;

    //Api access URL
    String url_str = "https://v6.exchangerate-api.com/v6/1349d6fe98a605b734c03999/latest/USD";

    public String rateCalc() throws IOException {
        // Making Request
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to JSON
        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        // Accessing object
        String rate = jsonobj.getAsJsonObject("conversion_rates").get("TRY").getAsString();

        return rate;
    }


    public rest(RequestFactory requestFactory, SearchService searchService, JiraAuthenticationContext authenticationContext) {
        this.requestFactory = requestFactory;
        this.issueManager= ComponentAccessor.getIssueManager();
        this.searchService = searchService;
        this.authenticationContext = authenticationContext;
    }
    
    @GET
    @Path("/child")
    public String child(@Context HttpServletRequest req, @Context HttpServletResponse resp) throws IOException {
        String rateString = "The current USD to TRY exchange rate is: " + rateCalc();
        return rateString;
    }



}
