
package cz.habarta.typescript.generator.emitter;

import cz.habarta.typescript.generator.TsProperty;
import cz.habarta.typescript.generator.TsType;
import cz.habarta.typescript.generator.util.Utils;

import java.lang.annotation.Annotation;
import java.util.List;


public class TsPropertyModel extends TsProperty implements Comparable<TsProperty> {

    public final boolean readonly;
    public final boolean ownProperty; // property exists directly on the bean, should not be inherited
    public final List<String> comments;
    public final List<Annotation> annotations;

    public TsPropertyModel(String name, TsType tsType, boolean readonly, boolean ownProperty, List<String> comments, List<Annotation> annotations) {
        super(name, tsType);
        this.readonly = readonly;
        this.comments = comments;
        this.ownProperty = ownProperty;
        this.annotations = Utils.listFromNullable(annotations);
    }

    public boolean isOwnProperty() {
        return ownProperty;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public TsPropertyModel setTsType(TsType type) {
        return new TsPropertyModel(getName(), type, readonly, ownProperty, getComments(), annotations);
    }

    @Override
    public int compareTo(TsProperty o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "TsPropertyModel{" + "name=" + name + ", tsType=" + tsType + '}';
    }

}
