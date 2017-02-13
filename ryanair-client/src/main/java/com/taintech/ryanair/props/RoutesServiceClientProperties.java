package com.taintech.ryanair.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
@ConfigurationProperties("ryanair.routes")
public class RoutesServiceClientProperties {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
