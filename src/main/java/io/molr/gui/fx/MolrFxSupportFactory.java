package io.molr.gui.fx;

import io.molr.gui.fx.conf.MolrFxSupportConfiguration;
import io.molr.gui.fx.support.MolrFxSupport;
import org.minifx.workbench.conf.MiniFxWorkbenchConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

/**
 * This class is intended for users that want to consistently reject the advantages of spring and thus do not want to
 * use spring configurations directly in their gui applications ;-) The usage of this is discouraged!
 * Instead, it is recommended to import the spring {@link MolrFxSupportConfiguration} directly into your own spring
 * configuration. This way, the {@link MolrFxSupport} can be autowired wherever you need it! ;-)
 * <p>
 * Enough of disclaimer ;-) For those who really want, they can use the {@link #newMolrFxSupport(Class[])} to create a
 * fresh instance, while you have to provide the configuration classes for molr as parameters (e.g. containing moles [and missions if local])
 */
public final class MolrFxSupportFactory {

    private MolrFxSupportFactory() {
        throw new UnsupportedOperationException("only static methods");
    }

    /**
     * Creates a new {@link MolrFxSupport} by instantiating an application context and looking up the bean. The
     * additional configuration classes given as parameters have to provide at least the mole to use. Otherwise,
     * the context cannot be loaded.
     *
     * @param configurationClasses The configuration classes, providing the essentials of molr
     * @return a fully configured support to use molr-specific gui elements in an elegant way.
     */
    public static MolrFxSupport newMolrFxSupport(Class<?>... configurationClasses) {
        @SuppressWarnings("resource") /* Closed automatically by the hook */
                AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ensureSingleFxSupportConfig(configurationClasses));

        /* Spring context register itself in the shutdown hook. It automatically keeps a reference to it this way */
        ctx.registerShutdownHook();

        return ctx.getBean(MolrFxSupport.class);
    }

    private static final Class<?>[] ensureSingleFxSupportConfig(Class<?>... configurationClasses) {
        Set<Class<?>> classes = new HashSet<>(Arrays.asList(configurationClasses));
        classes.add(MolrFxSupportConfiguration.class);
        return classes.toArray(new Class<?>[classes.size()]);
    }

}
