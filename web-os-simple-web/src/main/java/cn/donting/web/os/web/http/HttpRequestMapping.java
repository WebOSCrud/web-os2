package cn.donting.web.os.web.http;

import java.util.Objects;

public class HttpRequestMapping {

    private final HttpMethod httpMethod;
    private final String url;

    public HttpRequestMapping(HttpMethod httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequestMapping that = (HttpRequestMapping) o;
        return httpMethod == that.httpMethod && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, url);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return httpMethod+" "+url;
    }
}
