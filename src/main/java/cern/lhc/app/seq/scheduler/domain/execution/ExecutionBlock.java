/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.lhc.app.seq.scheduler.domain.execution;

import java.util.List;
import java.util.Optional;

import cern.lhc.app.seq.scheduler.domain.attributes.Attribute;

public interface ExecutionBlock {

    String name();

    List<ExecutionBlock> children();

    <A extends Attribute<?, ?>> Optional<A> get(Class<A> attributeType);

    default <T, A extends Attribute<T, A>> boolean has(A attribute) {
        // @formatter:off
        return get(attribute.markerClass())
                .map(Attribute::get)
                .map(a -> a.equals(attribute.get()))
                .orElse(false);
        // @formatter:on
    }
}
