package domain.viewmodel;

import java.util.Set;

public class ChannelMessageView extends ChannelView {

    private Set<MessageView> messages;

    public ChannelMessageView setMessages(Set<MessageView> messages) {
        this.messages = messages;
        return this;
    }

    public Set<MessageView> getMessages() {
        return messages;
    }
}
