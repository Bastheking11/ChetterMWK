package domain.entity;

import javax.persistence.*;
import java.util.Date;

// https://en.wikibooks.org/wiki/Java_Persistence/ManyToMany
@Entity
@Table
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User creator;
    private Date created = new Date();
    @Column(length = 2000)
    private String content;

    @ManyToOne
    private Channel channel;

    public Message(Channel channel, User creator, Date created, String content) {
        setCreator(creator);
        setCreated(created);
        setContent(content);
        setChannel(channel);
    }

    public Message() {
    }

    public Channel getChannel() {
        return channel;
    }

    public Message setChannel(Channel channel) {
        this.channel = channel;
        channel.addMessage(this);
        return this;
    }

    public long getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public Message setCreator(User creator) {
        this.creator = creator;
        creator.addMessage(this);
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Message setCreated(Date created) {
        this.created = created;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isMedia() {
        return this.content.matches("/\\[media=.*]/");
    }
}
