package com.veniture.servlet;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.query.Query;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Scanned
public class servlet extends HttpServlet {

    @JiraImport
    public IssueManager issueManager;
    @JiraImport
    private ProjectService projectService;
    @JiraImport
    private SearchService searchService;
    @JiraImport
    private TemplateRenderer templateRenderer;
    @JiraImport
    private JiraAuthenticationContext authenticationContext;
    @JiraImport
    private ConstantsManager constantsManager;
    @JiraImport
    public RequestFactory requestFactory;
    @JiraImport
    private ProjectManager projectManager;

    private static final String LIST_PROJECTS_TEMPLATE = "/templates/frontend.vm";
    private static final String NEW_PROJECT_TEMPLATE = "/templates/view.vm";
    private static final String EDIT_PROJECT_TEMPLATE = "/templates/edit.vm";

    public static final Logger logger = LoggerFactory.getLogger(servlet.class);

    public servlet(IssueManager issueManager,
                   SearchService searchService,
                   TemplateRenderer templateRenderer,
                   JiraAuthenticationContext authenticationContext,
                   ConstantsManager constantsManager,
                   RequestFactory requestFactory,
                   ProjectManager projectManager) {
        this.issueManager = issueManager;
        this.searchService = searchService;
        this.authenticationContext = authenticationContext;
        this.templateRenderer = templateRenderer;
        this.constantsManager = constantsManager;
        this.requestFactory = requestFactory;
        this.projectManager = projectManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String action = Optional.ofNullable(req.getParameter("actionType")).orElse("");

        Map<String, Object> context = new HashMap<>();
        resp.setContentType("text/html;charset=utf-8");
        switch (action) {
            case "new":
                templateRenderer.render(NEW_PROJECT_TEMPLATE, context, resp.getWriter());
                break;
            case "edit":
                ProjectService.GetProjectResult projectResult = projectService.getProjectByKey(authenticationContext.getLoggedInUser(),
                        req.getParameter("key"));
                context.put("project", projectResult.getProject());
                templateRenderer.render(EDIT_PROJECT_TEMPLATE, context, resp.getWriter());
                break;
            default:
                List<Project> projects = getProjects();
                context.put("projects", projects);
                templateRenderer.render(LIST_PROJECTS_TEMPLATE, context, resp.getWriter());
        }
    


    }
    private List<Project> getProjects() {
        return projectManager.getProjects();
    }
}
