package domain.viewmodel;

import domain.entity.User;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class UserView extends ViewModel<User> {

    private String username;
    private String email;

    private Set<PartyView> owned;
    private Set<MessageView> lastMessages;
    private Set<MemberView> subscriptions;

    private boolean administrator;

    private Date lastMessage;
    private int messageCount;

    @Override
    protected void setup(User root) {
        super.setup(root);

        lastMessages = Convert(root.getMessages().stream().limit(5).collect(Collectors.toSet()), MessageView.class, depth);
        root.getMessages().stream().findFirst().ifPresent(message -> lastMessage = message.getCreated());
        messageCount = root.getMessages().size();
    }

    public String getUsername() {
        return username;
    }

    public UserView setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserView setEmail(String email) {
        this.email = email;
        return this;
    }

    public Set<PartyView> getOwned() {
        return owned;
    }

    public UserView setOwned(Set<PartyView> owned) {
        this.owned = owned;
        return this;
    }

    public Set<MessageView> getLastMessages() {
        return lastMessages;
    }

    public UserView setLastMessages(Set<MessageView> lastMessages) {
        this.lastMessages = lastMessages;
        return this;
    }

    public Set<MemberView> getSubscriptions() {
        return subscriptions;
    }

    public UserView setSubscriptions(Set<MemberView> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public UserView setAdministrator(boolean administrator) {
        this.administrator = administrator;
        return this;
    }

    public Date getLastMessage() {
        return lastMessage;
    }

    public UserView setLastMessage(Date lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public UserView setMessageCount(int messageCount) {
        this.messageCount = messageCount;
        return this;
    }
}
