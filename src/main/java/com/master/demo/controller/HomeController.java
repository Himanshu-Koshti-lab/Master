package com.master.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private WebEndpointDiscoverer webEndpointDiscoverer;

    @GetMapping("/")
    public String index(Model model) {
        // Get all exposed web endpoints
        Collection<ExposableWebEndpoint> endpoints = webEndpointDiscoverer.getEndpoints();

        // Base path for actuator endpoints
        String basePath = "/actuator";

        // Create a resolver and get the links
        EndpointLinksResolver resolver = new EndpointLinksResolver(endpoints);
        Map<String, Link> links = resolver.resolveLinks(basePath);
        model.addAttribute("links", links);
        return "home";
    }
}
