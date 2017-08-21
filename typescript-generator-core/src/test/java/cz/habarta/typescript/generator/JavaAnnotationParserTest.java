
package cz.habarta.typescript.generator;

import cz.habarta.typescript.generator.parser.BeanModel;
import cz.habarta.typescript.generator.parser.Jackson1Parser;
import cz.habarta.typescript.generator.parser.JavaAnnotationParser;
import cz.habarta.typescript.generator.parser.Model;
import org.junit.Assert;
import org.junit.Test;

public class JavaAnnotationParserTest {

    @Test
    public void test() {
        final JavaAnnotationParser jacksonParser = getJavaAnnotationParser();
        final Class<?> bean = DummyBean.class;
        final Model model = jacksonParser.parseModel(bean);
        Assert.assertTrue(model.getBeans().size() > 0);
        final BeanModel beanModel = model.getBeans().get(0);
        Assert.assertEquals("DummyBean", beanModel.getOrigin().getSimpleName());
        Assert.assertTrue(beanModel.getProperties().size() > 0);
        Assert.assertEquals("firstProperty", beanModel.getProperties().get(0).getName());
        Assert.assertEquals("NotNull", beanModel.getProperties().get(0).getAnnotations().get(0).annotationType().getSimpleName());
    }

    private static JavaAnnotationParser getJavaAnnotationParser() {
        final Settings settings = new Settings();
        return new JavaAnnotationParser(settings, new DefaultTypeProcessor());
    }

}
