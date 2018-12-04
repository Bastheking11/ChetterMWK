package domain.viewmodel;

import domain.entity.Role;

public class RoleView extends ViewModel<Role> {

    private int index;
    private String name;
    private String color;

    public RoleView setIndex(int index) {
        this.index = index;
        return this;
    }

    public RoleView setName(String name) {
        this.name = name;
        return this;
    }

    public RoleView setColor(String color) {
        this.color = color;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((RoleView) obj).getName().equals(this.getName());
    }
}
