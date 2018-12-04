package domain.viewmodel;

import domain.entity.Message;

import java.util.Date;

public class MessageView extends ViewModel<Message> {

    private String content;
    private Date created;
    private boolean media;

    private ChannelView channel;
    private MemberView creator;

    @Override
    protected void setup(Message root) {
        super.setup(root);

        media = root.isMedia();

        root.getCreator().getSubscriptions().stream().filter(
                member -> member.getParty() == root.getChannel().getParty()
        ).findFirst().ifPresent(
                member -> creator = Convert(member, MemberView.class, depth)
        );
    }

    public MessageView setContent(String content) {
        this.content = content;
        return this;
    }

    public MessageView setCreated(Date created) {
        this.created = created;
        return this;
    }

    public MessageView setMedia(boolean media) {
        this.media = media;
        return this;
    }

    public MessageView setChannel(ChannelView channel) {
        this.channel = channel;
        return this;
    }

    public MessageView setCreator(MemberView creator) {
        this.creator = creator;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isMedia() {
        return media;
    }

    public ChannelView getChannel() {
        return channel;
    }

    public MemberView getCreator() {
        return creator;
    }
}
