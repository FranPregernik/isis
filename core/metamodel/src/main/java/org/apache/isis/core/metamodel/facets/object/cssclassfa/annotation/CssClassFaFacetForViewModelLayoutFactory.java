/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License. */

package org.apache.isis.core.metamodel.facets.object.cssclassfa.annotation;

import org.apache.isis.applib.annotation.ActionLayout.CssClassFaPosition;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.members.cssclassfa.CssClassFaFacet;
import org.apache.isis.core.metamodel.facets.members.cssclassfa.CssClassFaFacetAbstract;
import org.apache.isis.core.metamodel.facets.object.domainobjectlayout.CssClassFaFacetForViewModelLayoutAnnotation;

import com.google.common.base.Strings;

public class CssClassFaFacetForViewModelLayoutFactory extends CssClassFaFacetAbstract {

    public CssClassFaFacetForViewModelLayoutFactory(String value, CssClassFaPosition position, FacetHolder holder) {
        super(value, position, holder);
        // TODO Auto-generated constructor stub
    }

    public static CssClassFaFacet create(final DomainObjectLayout domainObjectLayout, final FacetHolder holder) {
        if (domainObjectLayout == null) {
            return null;
        }
        final String cssClassFa = Strings.emptyToNull(domainObjectLayout.cssClassFa());
        return (CssClassFaFacet) (cssClassFa != null ? new CssClassFaFacetForViewModelLayoutAnnotation(cssClassFa,
                holder) : null);
    }

    private CssClassFaFacetForViewModelLayoutFactory(final String value, final FacetHolder holder) {
        super(value, null, holder);
    }
}
