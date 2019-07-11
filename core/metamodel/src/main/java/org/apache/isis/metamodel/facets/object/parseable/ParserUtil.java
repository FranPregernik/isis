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

package org.apache.isis.metamodel.facets.object.parseable;

import static org.apache.isis.commons.internal.base._Casts.uncheckedCast;

import org.apache.isis.applib.adapters.Parser;
import org.apache.isis.commons.internal.base._Strings;
import org.apache.isis.config.IsisConfiguration;
import org.apache.isis.metamodel.commons.ClassUtil;
import org.apache.isis.metamodel.facetapi.FacetHolder;

public final class ParserUtil {

    private ParserUtil() {
    }

    public static final String PARSER_NAME_KEY_PREFIX = "isis.reflector.java.facets.parser.";
    public static final String PARSER_NAME_KEY_SUFFIX = ".parserName";

    public static String parserNameFromConfiguration(final Class<?> type, final IsisConfiguration configuration) {
        final String key = PARSER_NAME_KEY_PREFIX + type.getCanonicalName() + PARSER_NAME_KEY_SUFFIX;
        final String parserName = configuration.getString(key);
        return !_Strings.isNullOrEmpty(parserName) ? parserName : null;
    }

    public static Class<? extends Parser<?>> parserOrNull(final Class<?> candidateClass, final String classCandidateName) {
        
        final Class<? extends Parser<?>> type = candidateClass != null 
                ? uncheckedCast(ClassUtil.implementingClassOrNull(
                        candidateClass.getName(), Parser.class, FacetHolder.class)) 
                        : null;
        
        return type != null 
                ? type 
                        : uncheckedCast(ClassUtil.implementingClassOrNull(
                                classCandidateName, Parser.class, FacetHolder.class));
    }

}