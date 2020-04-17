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
package org.apache.isis.valuetypes.asciidoc.ui.components.prism;

import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import org.apache.isis.incubator.viewer.vaadin.ui.util.LocalResourceUtil;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PrismResources {

    @Getter(lazy = true) private static final LocalResourceUtil.ResourceDescriptor cssResourceReferenceVaa = 
            //LocalResourceUtil.ResourceDescriptor.webjars("prism/1.16.0/themes/prism.css");
            LocalResourceUtil.ResourceDescriptor.staticRoot("prism/css/prism.css");
    
    
    @Getter(lazy = true) private static final LocalResourceUtil.ResourceDescriptor jsResourceReferenceVaa =
            //LocalResourceUtil.ResourceDescriptor.webjars("prism/1.16.0/prism.js");
            LocalResourceUtil.ResourceDescriptor.staticRoot("prism/js/prism1.14.js");
    
    @Getter(lazy = true) private static final ResourceReference cssResourceReferenceWkt =
            //new ContextRelativeResourceReference("webjars/prism/1.16.0/themes/prism.css", false);
            new CssResourceReference(PrismResources.class, "css/prism.css");
    
    @Getter(lazy = true) private static final ResourceReference jsResourceReferenceWkt =
            //new ContextRelativeResourceReference("webjars/prism/1.16.0/prism.js", false);
            new JavaScriptResourceReference(PrismResources.class, "js/prism1.14.js");
    
}
