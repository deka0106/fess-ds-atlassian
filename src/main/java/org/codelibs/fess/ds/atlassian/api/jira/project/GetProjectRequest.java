/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.ds.atlassian.api.jira.project;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

import org.codelibs.fess.ds.atlassian.api.jira.JiraClient;
import org.codelibs.fess.ds.atlassian.api.jira.JiraRequest;

public class GetProjectRequest extends JiraRequest {

    private final String projectIdOrKey;
    private String[] expand;

    public GetProjectRequest(JiraClient jiraClient, String projectIdOrKey) {
        super(jiraClient);
        this.projectIdOrKey = projectIdOrKey;
    }

    @Override
    public GetProjectResponse execute() {
        try {
            final HttpRequest request = jiraClient.request()
                    .buildGetRequest(buildUrl(jiraClient.jiraHome(), projectIdOrKey, expand));
            final HttpResponse response = request.execute();
            final Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
            final String result = s.hasNext() ? s.next() : "";
            final ObjectMapper mapper = new ObjectMapper();
            final Map<String, Object> project = mapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            return new GetProjectResponse(project);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GetProjectRequest expand(String... expand) {
        this.expand = expand;
        return this;
    }

    protected GenericUrl buildUrl(final String jiraHome, final String projectIdOrKey, final String[] expand) {
        final GenericUrl url = new GenericUrl(jiraHome + "/rest/api/latest/project/" + projectIdOrKey);
        if (expand != null) {
            url.put("expand", String.join(",", expand));
        }
        return url;
    }

}