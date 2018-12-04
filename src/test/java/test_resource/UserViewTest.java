package test_resource;

import domain.entity.User;
import domain.viewmodel.ViewModel;

public class UserViewTest extends ViewModel<User> {
    private String username;
    private String email;
    private boolean administrator;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.split("@")[0];
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    @Override
    protected void setup(User root) {
        super.setup(root);
        administrator = false;
    }
}