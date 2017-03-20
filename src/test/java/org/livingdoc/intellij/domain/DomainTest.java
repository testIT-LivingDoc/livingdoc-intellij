package org.livingdoc.intellij.domain;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class DomainTest {

    @Test
    public void testForDomainCoverage() throws Exception {
        final String[] listPackages = new String[]{"info.novatec.testit.livingdoc.intellij.domain"};
        for (final String sPackage : listPackages) {
            final Set<Class<?>> classes = ClassUtils.getClasses(sPackage);
            if (classes != null) {
                for (final Class<?> cls : classes) {
                    try {
                        Assert.assertNotNull(BeanUtils.cloneBean(cls.newInstance()));
                    } catch (IllegalAccessException | InstantiationException
                            | InvocationTargetException | NoSuchMethodException error) {
                        throw new AssertionError(error);
                    }

                }
            }
        }
    }
}