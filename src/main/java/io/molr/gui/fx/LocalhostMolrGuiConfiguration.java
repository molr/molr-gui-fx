/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx;

import io.molr.mole.core.api.Mole;
import io.molr.mole.core.runnable.demo.conf.DemoRunnableLeafsConfiguration;
import io.molr.mole.remote.conf.LocalhostRestClientConfiguration;
//import io.molr.mole.remote.rest.ReconnectingMole;
import io.molr.mole.remote.rest.RestRemoteMole;
import io.molr.mole.server.conf.ObjectMapperConfig;
import io.molr.mole.server.demo.DemoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({ MolrGuiBaseConfiguration.class, DemoConfiguration.class })

//@Import({MolrGuiBaseConfiguration.class, LocalhostRestClientConfiguration.class, ObjectMapperConfig.class})

//@Import({MolrGuiBaseConfiguration.class})
public class LocalhostMolrGuiConfiguration {

//    @Bean
//    public Mole localhostRemoteMole() {
//        return new ReconnectingMole(new RestRemoteMole("http://localhost:8800"));
//        return new RestRemoteMole("http://vmla012:8800");//mole-aggregator
//    }

}
