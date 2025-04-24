package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

public class ConfluenceViewExpandable {

    private String webresource;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebresource() {
        return webresource;
    }

    public void setWebresource(String webresource) {
        this.webresource = webresource;
    }

    public ConfluenceViewExpandable() {
    }

    public ConfluenceViewExpandable(String webresource, String content) {
        this.webresource = webresource;
        this.content = content;
    }
}
