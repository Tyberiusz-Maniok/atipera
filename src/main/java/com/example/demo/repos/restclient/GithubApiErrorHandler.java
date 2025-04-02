package com.example.demo.repos.restclient;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

@Component
public class GithubApiErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            String message;
            String username = url.getPath().split("/")[2]; // /users/{username}/repos
            if (url.getPath().contains("users")) {
                message = MessageFormat.format("User {0} was not found", username);
            } else {
                // in rare case repository gets deleted or its visibility changes between requests, 404 error may occur on branches endpoint
                String repo = url.getPath().split("/")[3]; // /users/{username}/{repository}/branches
                message = MessageFormat.format("Repository {0}/{1} was not found", username, repo);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        } else {
            // as per task requirements only 404 error gets handled
            ResponseErrorHandler.super.handleError(url, method, response);
        }

    }
}
