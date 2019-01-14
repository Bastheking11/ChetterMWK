package domain.entity;

import domain.entity.Linked.Member;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@NamedQueries({
        @NamedQuery(name = "user:FindByUsername", query = "SELECT u FROM User u WHERE u.username LIKE :username"),
        @NamedQuery(name = "user:GetByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "user:Get", query = "SELECT u FROM User u")
})
@Cacheable(false)
public class User implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Email(message = "user:emailRegex")
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "\\S{6,}", message = "user:passwordRegex")
    private String password;

    @Column(nullable = false)
    private byte[] salt;

    @Column(nullable = false)
    @Pattern(regexp = "[A-Za-z0-9_.-]{3,}", message = "user:usernameRegex")
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("since desc")
    private Set<Member> subscriptions = new HashSet<>();
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private Set<Party> owned = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    private Set<Message> messages = new HashSet<>();

    private boolean administrator;

    public User(String email, String password, String username, Set<Member> subscriptions, Set<Party> owned, Set<Message> messages, boolean administrator) {
        setOwned(owned);
        setEmail(email);
        setPassword(password);
        setUsername(username);
        setSubscriptions(subscriptions);
        setMessages(messages);
        setAdministrator(administrator);
    }

    public User(String email, String password, String username, boolean administrator) {
        this(email, password, username, new HashSet<>(), new HashSet<>(), new HashSet<>(), administrator);
    }

    public User(String email, String password, String username) {
        this(email, password, username, false);
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public Party createParty(Party party) {
        Member m = new Member(this, party);

        this.addOwned(party);
        this.addSubscription(m);

        return party;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public Set<Member> getSubscriptions() {
        return subscriptions;
    }

    public User setSubscriptions(Set<Member> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    public User removeSubscription(Member subscription) {
        this.subscriptions.remove(subscription);
        return this;
    }

    public User addSubscription(Member subscription) {
        subscriptions.add(subscription);
        subscription.getParty().getSubscribers().add(subscription);
        return this;
    }

    public Set<Party> getOwned() {
        return owned;
    }

    public User setOwned(Set<Party> owned) {
        this.owned = owned;

        for (Party o : owned) {
            if (o.getOwner() != this) o.setOwner(this);
        }

        return this;
    }

    public User addOwned(Party owned) {
        this.owned.add(owned);
        if (owned.getOwner() != this)
            owned.setOwner(this);
        return this;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public User setMessages(Set<Message> messages) {
        this.messages = messages;

        for (Message m : messages) {
            if (m.getCreator() != this) m.setCreator(this);
        }

        return this;
    }

    public User addMessage(Message message) {
        this.messages.add(message);
        if (message.getCreator() != this) message.setCreator(this);

        return this;
    }

    public User removeMessage(Message message) {
        this.messages.remove(message);
        if (message.getCreator() == this) message.setCreator(null);

        return this;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public byte[] getSalt() {
        return salt;
    }

    public User setSalt(byte[] salt) {
        this.salt = salt;
        return this;
    }

    public User setAdministrator(boolean administrator) {
        this.administrator = administrator;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];

        random.nextBytes(salt);
        return salt;
    }

    public static String SHA512(String password, byte[] salt) {
        String hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16));
            }

            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }

    @Override
    public String getName() {
        return this.email;
    }
}
