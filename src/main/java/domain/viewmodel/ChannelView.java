package domain.viewmodel;

import domain.entity.Channel;

public class ChannelView extends ViewModel<Channel> {

    private String name;
    private String image;
    private String description;

    private PartyView party;
    private int message_count;

    @Override
    protected void setup(Channel root) {
        super.setup(root);

        message_count = root.getMessages().size();
    }

    public ChannelView setName(String name) {
        this.name = name;
        return this;
    }

    public ChannelView setImage(String image) {
        this.image = image;
        return this;
    }

    public ChannelView setDescription(String description) {
        this.description = description;
        return this;
    }

    public ChannelView setParty(PartyView party) {
        this.party = party;
        return this;
    }

    public ChannelView setMessage_count(int message_count) {
        this.message_count = message_count;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public PartyView getParty() {
        return party;
    }

    public int getMessage_count() {
        return message_count;
    }
}
