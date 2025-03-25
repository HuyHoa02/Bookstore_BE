package com.chris.bookstore.dto;

import java.util.Map;

public class DataMailDTO {
    private String to;
    private String subject;
    private String content;
    private Map<String, Object> props;

    public DataMailDTO(String to, String subject, String content, Map<String, Object> props) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.props = props;
    }

    public DataMailDTO() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}