
package cz.habarta.typescript.generator;

import javax.validation.constraints.NotNull;
import java.util.*;


@SuppressWarnings("rawtypes")
public class DummyBean {

    @NotNull
    public String firstProperty;
    public int intProperty;
    public Integer integerProperty;
    public boolean booleanProperty;
    public Date dateProperty;
    public String[] stringArrayProperty;
    public List<String> stringListProperty;
    public ArrayList<String> stringArrayListProperty;
    public DummyEnum dummyEnumProperty;
    public Map<String, String> stringMapProperty;
    public List<List<Integer>> listOfListOfIntegerProperty;
    public Map<String, DummyBean> mapOfDummyBeanProperty;
    public List rawListProperty;
    public Map rawMapProperty;
    public List<DummyEnum> listOfDummyEnumProperty;
    public Map<String, DummyEnum> mapOfDummyEnumProperty;
    public Map<String, List<Map<String, DummyEnum>>> mapOfListOfMapOfDummyEnumProperty;

}
