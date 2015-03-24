/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.core.runtime.services.eventbus;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.EventBus;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.runtime.services.RequestScopedService;
import org.apache.isis.core.runtime.services.eventbus.adapter.EventBusAdapterForAxonSimple;
import org.apache.isis.core.runtime.services.eventbus.adapter.EventBusAdapterForGuava;

/**
 * Holds common runtime logic for EventBusService implementations.
 */
public abstract class EventBusServiceDefault extends EventBusService {
    
    //region > register
    /**
     * {@inheritDoc}
     *
     * This service overrides the method to perform additional validation that (a) request-scoped services register
     * their proxies, not themselves, and (b) that singleton services are never registered after the event bus has
     * been created.
     *
     * <p>
     *     Note that we <i>do</i> allow for request-scoped services to register (their proxies) multiple times, ie at
     *     the beginning of each transaction.  Because the subscribers are stored in a set, these additional
     *     registrations are in effect ignored.
     * </p>
     */
    @Override
    public void register(final Object domainService) {
        if(domainService instanceof RequestScopedService) {
            // ok; allow to be registered multiple times (each xactn) since stored in a set.
        } else {
            if (Annotations.getAnnotation(domainService.getClass(), RequestScoped.class) != null) {
                throw new IllegalArgumentException("Request-scoped services must register their proxy, not themselves");
            }
            // a singleton
            if(this.eventBus != null) {
                // ... coming too late to the party.
                throw new IllegalStateException("Event bus has already been created; too late to register any further (singleton) subscribers");
            }
        }
        super.register(domainService);
    }
    
    //endregion

    //region > init, shutdown
    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> properties) {
        final String implementation = properties.get("org.apache.isis.eventbus.implementation");

        this.implementation =
                "axon".equalsIgnoreCase(implementation)
                        ? EventBusImplementation.AXON
                        : EventBusImplementation.GUAVA;
    }
    //endregion

    //region > newEventBus
    enum EventBusImplementation {
        GUAVA {
            @Override
            public EventBus create() {
                return new EventBusAdapterForGuava();
            }
        },
        AXON {
            @Override
            public EventBus create() {
                return new EventBusAdapterForAxonSimple();
            }
        };

        public abstract EventBus create();
    }
    private EventBusImplementation implementation;

    @Override
    protected org.apache.isis.applib.services.eventbus.EventBus newEventBus() {
        return implementation.create();
    }
    //endregion

}
