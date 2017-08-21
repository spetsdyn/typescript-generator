package cz.habarta.typescript.generator;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

public class AnnotationsTest {

    private static void testOutput(Class<?> inputClass, String expected) {
        final Settings settings = TestUtils.settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.jsonLibrary = JsonLibrary.annotations;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(inputClass));
        Assert.assertEquals(expected.replace('\'', '"'), output.trim());
    }

    @Test
    public void testAnnotationAtClassLevel() {
        testOutput(OnClass.class,
                   "@AnnotationWithCustomValue\n" +
                           "class OnClass {\n" +
                           "}\n" +
                           "\n" +
                           "interface " +
                           "AnnotationWithCustomValue {\n" +
                           "}");
    }

    // TODO implement annotaions parsing at method level
    //    @Test
    //    public void testAnnotationAtMethodLevel() {
    //        testOutput(OnMethod.class,
    //                "@AnnotationWithCustomValue\n"
    //                        + "class OnMethod {\n"
    //                        + "}");
    //    }

    @Test
    public void testAnnotationAtParameterLevel() {
        testOutput(OnParameter.class,
                   "class OnParameter {\n" +
                           "    @AnnotationWithDefaultValue\n" +
                           "    @AnnotationWithCustomValue\n"
                           + "    parameter: string;\n" + "}\n" +
                           "\n" +
                           "interface AnnotationWithDefaultValue {\n" +
                           "}\n" +
                           "\n" +
                           "interface AnnotationWithCustomValue {\n" +
                           "}");
    }

    @Test
    public void testAnnotationOnInheritance() {
        testOutput(OnInheritance.class,
                   "@AnnotationWithCustomValue\n" +
                           "class OnClass {\n" +
                           "}\n" +
                           "\n" +
                           "class OnInheritance " +
                           "extends" + " OnClass {\n" +
                           "    @AnnotationWithDefaultValue\n" +
                           "    @AnnotationWithCustomValue\n" +
                           "    parameter: string;\n" + "}\n" +
                           "\n" +
                           "interface " +
                           "AnnotationWithDefaultValue {\n" +
                           "}\n" +
                           "\n" +
                           "interface AnnotationWithCustomValue {\n" +
                           "}");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({TYPE, METHOD, PARAMETER, FIELD})
    public @interface AnnotationWithDefaultValue {
        String value() default "yup";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({TYPE, METHOD, PARAMETER, FIELD})
    public @interface AnnotationWithCustomValue {
        String value() default "";
    }

    @AnnotationWithCustomValue("yay")
    private static abstract class OnClass {
    }

    //    private static abstract class OnMethod {
    //        @AnnotationWithDefaultValue
    //        @AnnotationWithCustomValue("yay")
    //        public String getMethod() {
    //            return "";
    //        }
    //    }

    private static abstract class OnParameter {
        @AnnotationWithDefaultValue
        @AnnotationWithCustomValue("yay")
        private String parameter;

    }

    private static abstract class OnInheritance extends OnClass {
        @AnnotationWithDefaultValue
        @AnnotationWithCustomValue("yay")
        private String parameter;

    }

}
