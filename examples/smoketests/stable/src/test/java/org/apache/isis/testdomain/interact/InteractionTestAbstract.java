/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.testdomain.interact;

import java.util.Collections;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.commons.internal.base._NullSafe;
import org.apache.isis.core.commons.internal.base._Strings;
import org.apache.isis.core.commons.internal.collections._Arrays;
import org.apache.isis.core.metamodel.interactions.managed.ActionInteraction;
import org.apache.isis.core.metamodel.interactions.managed.CollectionInteraction;
import org.apache.isis.core.metamodel.interactions.managed.PropertyInteraction;
import org.apache.isis.core.metamodel.objectmanager.ObjectManager;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.runtime.iactn.IsisInteractionFactory;
import org.apache.isis.testing.integtestsupport.applib.IsisIntegrationTestAbstract;

import lombok.val;

abstract class InteractionTestAbstract extends IsisIntegrationTestAbstract {
    
    @Inject protected ObjectManager objectManager;
    @Inject protected IsisInteractionFactory interactionFactory;
    
    protected ActionInteraction startActionInteractionOn(Class<?> type, String actionId, Where where) {
        val viewModel = factoryService.viewModel(type);
        val managedObject = objectManager.adapt(viewModel);
        return ActionInteraction.start(managedObject, actionId, where);
    }
    
    protected PropertyInteraction startPropertyInteractionOn(Class<?> type, String propertyId, Where where) {
        val viewModel = factoryService.viewModel(type);
        val managedObject = objectManager.adapt(viewModel);
        return PropertyInteraction.start(managedObject, propertyId, where);
    }
    
    protected CollectionInteraction startCollectionInteractionOn(Class<?> type, String collectionId, Where where) {
        val viewModel = factoryService.viewModel(type);
        val managedObject = objectManager.adapt(viewModel);
        return CollectionInteraction.start(managedObject, collectionId, where);
    }

    protected void assertMetamodelValid() {
        val specLoader = objectManager.getMetaModelContext().getSpecificationLoader(); 
        assertEquals(Collections.<String>emptyList(), specLoader.getValidationResult().getMessages());    
    }
    
    protected void assertComponentWiseEquals(Object a, Object b) {
        
        val array1 = _NullSafe.streamAutodetect(a)
            .collect(_Arrays.toArray(Object.class));
        val array2 = _NullSafe.streamAutodetect(b)
            .collect(_Arrays.toArray(Object.class));
        
        assertArrayEquals(array1, array2);
        
    }
    
    protected void assertComponentWiseUnwrappedEquals(Object a, Object b) {
        
        val array1 = _NullSafe.streamAutodetect(a)
            .map(element->(element instanceof ManagedObject) 
                    ? ((ManagedObject)element).getPojo()
                    : element)
            .collect(_Arrays.toArray(Object.class));
        
        val array2 = _NullSafe.streamAutodetect(b)
                .map(element->(element instanceof ManagedObject) 
                        ? ((ManagedObject)element).getPojo()
                        : element)
                .collect(_Arrays.toArray(Object.class));
        
        assertArrayEquals(array1, array2);
        
    }
    
    protected void assertEmpty(Object x) {
        if(x instanceof CharSequence) {
            assertTrue(_Strings.isEmpty((CharSequence)x));
            return;
        }
        assertEquals(0L, _NullSafe.streamAutodetect(x).count());
    }
    
    protected void assertDoesIncrement(Supplier<LongAdder> adder, Runnable runnable) {
        final int eventCount0 = adder.get().intValue();
        runnable.run();
        final int eventCount1 = adder.get().intValue();
        assertEquals(eventCount0 + 1, eventCount1);
    }
    
    protected void assertDoesNotIncrement(Supplier<LongAdder> adder, Runnable runnable) {
        final int eventCount0 = adder.get().intValue();
        runnable.run();
        final int eventCount1 = adder.get().intValue();
        assertEquals(eventCount0, eventCount1);
    }
    
    
}
