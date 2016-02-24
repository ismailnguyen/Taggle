package com.gnr.esgi.taggle.util;

import com.gnr.esgi.taggle.parser.HtmlParser;

public class URLBuilder {

    private String url;

    public URLBuilder(String url) {
        this.url = url;
    }

    public String getUrl() {
        return clean(url);
    }

    public void addParameter(String key, Object value) {
        StringBuilder builder = new StringBuilder(url);

        builder.append(key);
        builder.append("=");
        builder.append(value.toString());
        builder.append("&");

        url = builder.toString();
    }

    private String clean(String url) {
        url = HtmlParser.escapeSpace(url);

        if(url.endsWith("&"))
            url = url.substring(0, url.lastIndexOf("&"));

        return url;
    }
}
