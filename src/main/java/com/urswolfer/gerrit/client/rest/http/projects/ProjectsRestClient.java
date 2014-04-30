/*
 * Copyright 2013-2014 Urs Wolfer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urswolfer.gerrit.client.rest.http.projects;

import com.google.gerrit.extensions.api.projects.Projects;
import com.google.gerrit.extensions.common.ProjectInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.urswolfer.gerrit.client.rest.http.GerritRestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Urs Wolfer
 */
public class ProjectsRestClient extends Projects.NotImplemented implements Projects {

    private final GerritRestClient gerritRestClient;

    public ProjectsRestClient(GerritRestClient gerritRestClient) {
        this.gerritRestClient = gerritRestClient;
    }

    @Override
    public List<ProjectInfo> list() throws RestApiException {
        String request = "/projects/";
        JsonElement result = gerritRestClient.getRequest(request);
        if (result == null) {
            return Collections.emptyList();
        }
        return parseProjectInfos(result);
    }

    private List<ProjectInfo> parseProjectInfos(JsonElement result) throws RestApiException {
        List<ProjectInfo> repositories = new ArrayList<ProjectInfo>();
        final JsonObject jsonObject = result.getAsJsonObject();
        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (!element.getValue().isJsonObject()) {
                throw new RestApiException(String.format("This element should be a JsonObject: %s%nTotal JSON response: %n%s", element, result));
            }
            repositories.add(parseSingleRepositoryInfo(element.getValue().getAsJsonObject()));

        }
        return repositories;
    }

    private ProjectInfo parseSingleRepositoryInfo(JsonObject result) {
        return gerritRestClient.getGson().fromJson(result, ProjectInfo.class);
    }

    protected GerritRestClient getGerritRestClient() {
        return gerritRestClient;
    }
}
