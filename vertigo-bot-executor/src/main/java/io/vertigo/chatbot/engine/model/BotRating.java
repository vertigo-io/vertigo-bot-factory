package io.vertigo.chatbot.engine.model;

/**
 * @author cmarechal
 * @created 13/11/2023 - 09:41
 * @project vertigo-bot-factory
 */
public class BotRating {

    private boolean enabled;

    private BotRatingType type;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public BotRatingType getType() {
        return type;
    }

    public void setType(BotRatingType type) {
        this.type = type;
    }
}
