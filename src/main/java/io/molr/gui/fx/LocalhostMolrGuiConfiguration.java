/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package io.molr.gui.fx;

import io.molr.mole.remote.conf.LocalhostRestClientConfiguration;
import org.springframework.context.annotation.Import;

@Import({MolrGuiBaseConfiguration.class, LocalhostRestClientConfiguration.class})
public class LocalhostMolrGuiConfiguration {

}
