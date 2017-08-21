package cz.habarta.typescript.generator.parser;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.habarta.typescript.generator.Settings;
import cz.habarta.typescript.generator.TypeProcessor;
import cz.habarta.typescript.generator.compiler.EnumKind;
import cz.habarta.typescript.generator.compiler.EnumMemberModel;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaAnnotationParser extends ModelParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JavaAnnotationParser(Settings settings, TypeProcessor typeProcessor) {
        super(settings, typeProcessor);
        objectMapper.registerModules(ObjectMapper.findModules(settings.classLoader));
    }

    @Override
    protected DeclarationModel parseClass(SourceType<Class<?>> sourceClass) {
        if (sourceClass.type.isEnum()) {
            return parseEnumOrObjectEnum(sourceClass);
        }
        if (sourceClass.type.isAnnotation()) {
            return parseAnnotaion(sourceClass);
        }
        return parseBean(sourceClass);
    }

    private BeanModel parseBean(SourceType<Class<?>> sourceClass) {
        final List<PropertyModel> properties = new ArrayList<>();

        for (Field field : sourceClass.type.getDeclaredFields()) {
            Type propertyType = field.getGenericType();
            boolean optional = false;
            for (Class<? extends Annotation> optionalAnnotation : settings.optionalAnnotations) {
                if (field.getAnnotation(optionalAnnotation) != null) {
                    optional = true;
                    break;
                }
            }
            List<Annotation> annotations = new ArrayList<>();
            for (Annotation annotation : field.getAnnotations()) {
                annotations.add(annotation);
                addBeanToQueue(new SourceType<>(annotation.annotationType(), sourceClass.type, "<annotation>"));
            }
            properties.add(processTypeAndCreateProperty(field.getName(), propertyType, optional, sourceClass.type, null,
                                                        null, annotations));
        }

        final Type superclass = sourceClass.type.getGenericSuperclass() == Object.class ? null : sourceClass.type
                .getGenericSuperclass();
        if (superclass != null) {
            addBeanToQueue(new SourceType<>(superclass, sourceClass.type, "<superClass>"));
        }
        final List<Type> interfaces = Arrays.asList(sourceClass.type.getGenericInterfaces());
        for (Type aInterface : interfaces) {
            addBeanToQueue(new SourceType<>(aInterface, sourceClass.type, "<interface>"));
        }
        final List<Annotation> annotations = Arrays.asList(sourceClass.type.getAnnotations());
        for (Annotation annotation : annotations) {
            addBeanToQueue(new SourceType<>(annotation.annotationType(), sourceClass.type, "<annotation>"));
        }
        return new BeanModel(sourceClass.type, superclass, null, null, null, interfaces, properties, null, annotations);
    }

    private DeclarationModel parseEnumOrObjectEnum(SourceType<Class<?>> sourceClass) {

        final List<EnumMemberModel> enumMembers = new ArrayList<>();
        if (sourceClass.type.isEnum()) {
            final Class<?> enumClass = (Class<?>) sourceClass.type;

            try {
                Method valueMethod = null;
                final BeanInfo beanInfo = Introspector.getBeanInfo(enumClass);
                for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                    final Method readMethod = propertyDescriptor.getReadMethod();
                    if (readMethod.isAnnotationPresent(JsonValue.class)) {
                        valueMethod = readMethod;
                    }
                }

                int index = 0;
                for (Field field : enumClass.getFields()) {
                    if (field.isEnumConstant()) {
                        final String value = getStringEnumValue(field, valueMethod);
                        enumMembers.add(new EnumMemberModel(field.getName(), value, null));

                    }
                }
            } catch (Exception e) {
                System.out.println(String.format("Cannot get enum values for '%s' enum", enumClass.getName()));
                e.printStackTrace(System.out);
            }
        }

        return new EnumModel(sourceClass.type, EnumKind.StringBased, enumMembers, null);
    }

    private DeclarationModel parseAnnotaion(SourceType<Class<?>> sourceClass) {
        return new BeanModel(sourceClass.type, null, null, null, null, null, null, null, null);
    }

    private Number getNumberEnumValue(Field field, Method valueMethod, int index) throws Exception {
        if (valueMethod != null) {
            final Object valueObject = invokeJsonValueMethod(field, valueMethod);
            if (valueObject instanceof Number) {
                return (Number) valueObject;
            }
        }
        return index;
    }

    private String getStringEnumValue(Field field, Method valueMethod) throws Exception {
        if (valueMethod != null) {
            final Object valueObject = invokeJsonValueMethod(field, valueMethod);
            if (valueObject instanceof String) {
                return (String) valueObject;
            }
        }
        return field.getName();
    }

    private Object invokeJsonValueMethod(Field field, Method valueMethod) throws ReflectiveOperationException {
        field.setAccessible(true);
        final Object constant = field.get(null);
        valueMethod.setAccessible(true);
        final Object valueObject = valueMethod.invoke(constant);
        return valueObject;
    }

}
