package domain.viewmodel;

import domain.utility.Converter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
public abstract class ViewModel<T> implements Serializable {

    @XmlTransient
    private T root;

    @XmlTransient
    int depth = 3;

    private long id;

    public long getId() {
        return id;
    }

    public ViewModel<T> setId(long id) {
        this.id = id;
        return this;
    }

    public String getRootType() {
        return root.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && ((ViewModel) obj).getId() == this.getId();
    }

    protected void setup(T root) {
        Field[] attributes = this.getClass().getDeclaredFields();
        this.root = root;

        for (Field field : attributes)
            try {
                Converter convert = field.getDeclaredAnnotation(Converter.class);

                Object value = this.fieldFromRoot(field);

                if (value != null && convert != null && !convert.ignore()) {
                    try {
                        Class<?> conv = convert.converter() == void.class ? field.getType() : convert.converter();
                        Method m = conv.getMethod("Convert", value.getClass());

                        value = m.invoke(null, value);
                    } catch (NoSuchMethodException | InvocationTargetException ignored) {
                    }
                }

                if (value != null || (convert != null && convert.nullable())) {
                    this.setField(field, value);
                }

            } catch (IllegalAccessException ignored) {
            }
    }

    private void setField(Field field, Object value) throws IllegalAccessException {
        String name = field.getName();

        if (
                value != null &&
                        (
                                field.getType().getSuperclass() == ViewModel.class ||
                                        (
                                                field.getType() == Set.class &&
                                                        ViewModel.class.isAssignableFrom((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])
                                        )
                        ) &&
                        value.getClass() != field.getType()
        ) {
            if (depth <= 0) return;

            if (ViewModel.class.isAssignableFrom(field.getType())) value = ViewModel.Convert(value,

                    field.getType().asSubclass(ViewModel.class),

                    depth - 1);
            else value = ViewModel.Convert((Set) value,

                    ((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]
                    ).asSubclass(ViewModel.class),

                    depth - 1);
        }

        if (value != null)
            try {
                Class<?> n = value.getClass();
                if (Set.class.isAssignableFrom(value.getClass()))
                    n = Set.class;
                else if (Boolean.class.isAssignableFrom(value.getClass()))
                    n = boolean.class;

                this.getClass().getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), n).invoke(this, value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                field.set(this, value);
            }
    }

    private Object fieldFromRoot(Field field) {
        Converter convert = field.getDeclaredAnnotation(Converter.class);
        String name = convert != null && !convert.name().isEmpty() ? convert.name() : field.getName();
        try {
            return this.rootedMethod((field.getType() == boolean.class ? "is" : "get") + name.substring(0, 1).toUpperCase() + name.substring(1));
        } catch (Exception ignored) {
            try {
                return this.rootedField(name);
            } catch (Exception ignored2) {
            }
        }
        return null;
    }

    private Object rootedMethod(String name) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = this.root.getClass().getMethod(name);
        return method.invoke(this.root);
    }

    private Object rootedField(String name) throws NoSuchFieldException, IllegalAccessException {
        return this.getClass().getField(name).get(this.root);
    }

    public static <T, S extends ViewModel> S Convert(T root, Class<S> view) {
        return ViewModel.Convert(root, view, 2);
    }

    static <T, S extends ViewModel> S Convert(T root, Class<S> view, int depth) {
        try {
            S t = view.newInstance();
            t.depth = depth;
            t.setup(root);
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    public static <T, S extends ViewModel> Set<S> Convert(Set<T> roots, Class<S> view) {
        return ViewModel.Convert(roots, view, 2);
    }

    static <T, S extends ViewModel> Set<S> Convert(Set<T> roots, Class<S> view, int depth) {
        Set<S> channels = new HashSet<>();

        try {
            for (T root : roots) {
                S t = view.newInstance();
                t.depth = depth;
                t.setup(root);
                channels.add(t);
            }
        } catch (IllegalAccessException | InstantiationException ignored) {
        }

        return channels;
    }

}
