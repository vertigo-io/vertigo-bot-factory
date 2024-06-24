package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

import java.util.Map;

public class ConfluenceView {

    private String value;

    private String representation;

    private Map<String, Object> _expandable;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public Map<String, Object> get_expandable() {
        return _expandable;
    }

    public void set_expandable(Map<String, Object> _expandable) {
        this._expandable = _expandable;
    }

    public ConfluenceView() {
    }

    public ConfluenceView(String value, String representation, Map<String, Object> _expandable) {
        this.value = value;
        this.representation = representation;
        this._expandable = _expandable;
    }
}
