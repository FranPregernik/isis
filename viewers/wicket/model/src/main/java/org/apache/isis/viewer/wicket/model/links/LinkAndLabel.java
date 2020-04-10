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
package org.apache.isis.viewer.wicket.model.links;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import org.apache.wicket.markup.html.link.AbstractLink;

import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.core.commons.internal.collections._Lists;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.viewer.common.model.action.ActionUiMetaModel;
import org.apache.isis.viewer.common.model.action.ActionUiModel;
import org.apache.isis.viewer.wicket.model.models.EntityModel;

public class LinkAndLabel extends ActionUiModel<AbstractLink> implements Serializable  {

    private static final long serialVersionUID = 1L;
    
    public static LinkAndLabel newLinkAndLabel(
            final Function<ActionUiModel<AbstractLink>, AbstractLink> uiComponentFactory,
            final String named,
            final EntityModel actionHolderModel,
            final ObjectAction objectAction) {
        return new LinkAndLabel(uiComponentFactory, named, actionHolderModel, objectAction);
    }
    
    protected LinkAndLabel(
            final Function<ActionUiModel<AbstractLink>, AbstractLink> uiComponentFactory,
            final String named,
            final EntityModel actionHolderModel,
            final ObjectAction objectAction) {
        super(uiComponentFactory, named, actionHolderModel, objectAction);
    }

    public static List<LinkAndLabel> positioned(List<LinkAndLabel> list, Position pos) {
        return _Lists.filter(list, ActionUiMetaModel.positioned(pos, LinkAndLabel::getActionUiMetaModel));
    }

}
