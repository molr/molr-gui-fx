package io.molr.gui.fx.conf;

import io.molr.gui.fx.support.MolrFxSupport;
import io.molr.gui.fx.support.MolrFxSupportImpl;
import io.molr.mole.core.api.Mole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MolrFxSupportConfiguration {

    @Autowired
    private Mole mole;

    @Bean
    public MolrFxSupport molrFxSupport() {
        return new MolrFxSupportImpl(mole);
    }
}
