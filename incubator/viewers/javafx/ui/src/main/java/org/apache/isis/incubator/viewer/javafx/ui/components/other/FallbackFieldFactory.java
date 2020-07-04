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
package org.apache.isis.incubator.viewer.javafx.ui.components.other;

import org.springframework.core.annotation.Order;

import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.OrderPrecedence;
import org.apache.isis.incubator.viewer.javafx.ui.components.UiComponentHandlerFx;
import org.apache.isis.incubator.viewer.javafx.ui.components.form.FormField;
import org.apache.isis.incubator.viewer.javafx.ui.components.form.SimpleFormField;
import org.apache.isis.viewer.common.model.binding.UiComponentFactory.Request;

import javafx.scene.control.Label;
import lombok.val;

@org.springframework.stereotype.Component
@Order(OrderPrecedence.LAST)
public class FallbackFieldFactory implements UiComponentHandlerFx {
    
    @Override
    public boolean isHandling(Request request) {
        return true; // the last handler in the chain
    }

    @Override
    public FormField handle(Request request) {
        
        val spec = request.getObjectFeature().getSpecification();
        val uiField = new Label(spec.getCorrespondingClass().getSimpleName() + " type not handled");
        val uiLabel = new Label(request.getFeatureLabel());
        
        return new SimpleFormField(LabelPosition.TOP, uiLabel, uiField);
    }

}
