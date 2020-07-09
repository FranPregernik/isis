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

package org.apache.isis.extensions.viewer.wicket.pdfjs.ui.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.commons.internal.collections._Lists;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacet;
import org.apache.isis.core.metamodel.facets.objectvalue.labelat.LabelAtFacet;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.viewer.common.model.object.ObjectUiModel.RenderingHint;
import org.apache.isis.viewer.wicket.model.links.LinkAndLabel;
import org.apache.isis.viewer.wicket.model.models.ScalarModel;
import org.apache.isis.viewer.wicket.ui.components.actionmenu.entityactions.AdditionalLinksPanel;
import org.apache.isis.viewer.wicket.ui.components.scalars.ScalarPanelAbstract;
import org.apache.isis.viewer.wicket.ui.components.scalars.TextFieldValueModel.ScalarModelProvider;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

import lombok.val;

/**
 * Adapter for {@link PanelAbstract panel}s that use a {@link ScalarModel} as
 * their backing model.
 *
 * <p>
 * Supports the concept of being {@link Rendering#COMPACT} (eg within a table) or
 * {@link Rendering#REGULAR regular} (eg within a form).
 *
 * <p>
 *     REVIEW: this has been replaced by {@link ScalarPanelAbstract} and is unused by the core framework.
 *     It is however still used by some wicket addons (specifically, pdfjs).
 * </p>
 */
abstract class ScalarPanelAbstractLegacy<T extends ScalarModel> 
extends PanelAbstract<T> 
implements ScalarModelProvider {

    private static final long serialVersionUID = 1L;

    protected static final String ID_SCALAR_IF_REGULAR = "scalarIfRegular";
    protected static final String ID_SCALAR_NAME = "scalarName";
    protected static final String ID_SCALAR_VALUE = "scalarValue";

    protected static final String ID_SCALAR_IF_COMPACT = "scalarIfCompact";

    private static final String ID_ASSOCIATED_ACTION_LINKS_BELOW = "associatedActionLinksBelow";
    private static final String ID_ASSOCIATED_ACTION_LINKS_RIGHT = "associatedActionLinksRight";

    public enum CompactType {
        INPUT_CHECKBOX,
        SPAN
    }

    public enum Rendering {
        /**
         * Does not show labels, eg for use in tables
         */
        COMPACT {
            @Override
            public String getLabelCaption(final LabeledWebMarkupContainer labeledContainer) {
                return "";
            }

            @Override
            public void buildGui(final ScalarPanelAbstractLegacy panel) {
                panel.getComponentForRegular().setVisible(false);
            }

            @Override
            public Where getWhere() {
                return Where.PARENTED_TABLES;
            }
        },
        /**
         * Does show labels, eg for use in forms.
         */
        REGULAR {
            @Override
            public String getLabelCaption(final LabeledWebMarkupContainer labeledContainer) {
                return labeledContainer.getLabel().getObject();
            }

            @Override
            public void buildGui(final ScalarPanelAbstractLegacy panel) {
                panel.getLabelForCompact().setVisible(false);
            }

            @Override
            public Where getWhere() {
                return Where.OBJECT_FORMS;
            }
        };

        public abstract String getLabelCaption(LabeledWebMarkupContainer labeledContainer);

        public abstract void buildGui(ScalarPanelAbstractLegacy panel);

        public abstract Where getWhere();

        private static Rendering renderingFor(RenderingHint renderingHint) {
            return renderingHint.isInTable()? Rendering.COMPACT: Rendering.REGULAR;
        }
    }

    protected Component componentIfCompact;
    private Component componentIfRegular;
    protected final ScalarModel scalarModel;


    public ScalarPanelAbstractLegacy(final String id, final ScalarModel scalarModel) {
        super(id, scalarModel);
        this.scalarModel = scalarModel;
    }

    protected Fragment getCompactFragment(CompactType type) {
        Fragment compactFragment;
        switch (type) {
        case INPUT_CHECKBOX:
            compactFragment = new Fragment("scalarIfCompact", "compactAsInputCheckbox", ScalarPanelAbstractLegacy.this);
            break;
        case SPAN:
        default:
            compactFragment = new Fragment("scalarIfCompact", "compactAsSpan", ScalarPanelAbstractLegacy.this);
            break;
        }
        return compactFragment;
    }

    protected Rendering getRendering() {
        return Rendering.renderingFor(getModel().getRenderingHint());
    }

    protected Component getLabelForCompact() {
        return componentIfCompact;
    }

    public Component getComponentForRegular() {
        return componentIfRegular;
    }

    @Override
    protected void onBeforeRender() {

        if ((!hasBeenRendered() || alwaysRebuildGui())) {
            buildGui();
        }

        final ScalarModel scalarModel = getModel();

        final String disableReasonIfAny = scalarModel.whetherDisabled();
        if (disableReasonIfAny != null) {
            if(disableReasonIfAny.contains("Always disabled")) {
                onBeforeRenderWhenViewMode();
            } else {
                onBeforeRenderWhenDisabled(disableReasonIfAny.toString());
            }
        } else {
            if (scalarModel.isViewMode()) {
                onBeforeRenderWhenViewMode();
            } else {        
                onBeforeRenderWhenEnabled();
            }
        }

        super.onBeforeRender();
    }

    /**
     * hook for highly dynamic components, eg conditional choices.
     *
     * <p>
     * Returning <tt>true</tt> means that the component is always rebuilt prior to
     * every {@link #onBeforeRender() render}ing.
     */
    protected boolean alwaysRebuildGui() {
        return false;
    }

    /**
     * Builds GUI lazily prior to first render.
     *
     * <p>
     * This design allows the panel to be configured first.
     *
     * @see #onBeforeRender()
     */
    private void buildGui() {

        // REVIEW: this is nasty, both write to the same entityLink field
        // even though only one is used
        componentIfCompact = addComponentForCompact();
        componentIfRegular = addComponentForRegular();

        getRendering().buildGui(this);
        addCssForMetaModel();

        if(!subscribers.isEmpty()) {
            addFormComponentBehavior(new ScalarUpdatingBehavior());
        }
    }

    protected class ScalarUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {
        private static final long serialVersionUID = 1L;

        private ScalarUpdatingBehavior() {
            super("change");
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            for (ScalarModelSubscriberLegacy subscriber : subscribers) {
                subscriber.onUpdate(target, ScalarPanelAbstractLegacy.this);
            }

            // hmmm... this doesn't seem to be picked up...
            target.appendJavaScript(
                    String.format("Wicket.Event.publish(Isis.Topic.FOCUS_FIRST_ACTION_PARAMETER, '%s')", getMarkupId()));
        }

        @Override
        protected void onError(AjaxRequestTarget target, RuntimeException e) {
            super.onError(target, e);
            for (ScalarModelSubscriberLegacy subscriber : subscribers) {
                subscriber.onError(target, ScalarPanelAbstractLegacy.this);
            }
        }
    }

    /**
     * Mandatory hook.
     */
    protected abstract void addFormComponentBehavior(Behavior behavior);

    private void addCssForMetaModel() {
        final String cssForMetaModel = getModel().getCssClass();
        if (cssForMetaModel != null) {
            add(new AttributeAppender("class", Model.of(cssForMetaModel), " "));
        }

        ScalarModel model = getModel();
        final CssClassFacet facet = model.getFacet(CssClassFacet.class);
        if(facet != null) {
            val parentAdapter = model.getParentUiModel().getManagedObject();
            final String cssClass = facet.cssClass(parentAdapter);
            CssClassAppender.appendCssClassTo(this, cssClass);
        }
    }

    /**
     * Mandatory hook method to build the component to render the model when in
     * {@link Rendering#REGULAR regular} format.
     */
    protected abstract MarkupContainer addComponentForRegular();

    protected abstract Component addComponentForCompact();


    /**
     * Optional hook.
     */
    protected void onBeforeRenderWhenViewMode() {
    }

    /**
     * Optional hook.
     */
    protected void onBeforeRenderWhenDisabled(final String disableReason) {
    }

    /**
     * Optional hook.
     */
    protected void onBeforeRenderWhenEnabled() {
    }

    /**
     * Applies the {@literal @}{@link LabelAtFacet} and also CSS based on
     * whether any of the associated actions have {@literal @}{@link org.apache.isis.applib.annotation.ActionLayout layout} positioned to
     * the {@link org.apache.isis.applib.annotation.ActionLayout.Position#RIGHT right}.
     *
     * @param markupContainer The form group element
     * @param entityActionLinks
     */
    protected void addPositioningCssTo(final MarkupContainer markupContainer, final List<LinkAndLabel> entityActionLinks) {
        CssClassAppender.appendCssClassTo(markupContainer, determinePropParamLayoutCss(getModel()));
        CssClassAppender.appendCssClassTo(markupContainer, determineActionLayoutPositioningCss(entityActionLinks));
    }

    protected void addEntityActionLinksBelowAndRight(final MarkupContainer labelIfRegular, final List<LinkAndLabel> entityActions) {
        final List<LinkAndLabel> entityActionsBelow = LinkAndLabel.positioned(entityActions, ActionLayout.Position.BELOW);
        AdditionalLinksPanel.addAdditionalLinks(labelIfRegular, ID_ASSOCIATED_ACTION_LINKS_BELOW, entityActionsBelow, AdditionalLinksPanel.Style.INLINE_LIST);

        final List<LinkAndLabel> entityActionsRight = LinkAndLabel.positioned(entityActions, ActionLayout.Position.RIGHT);
        AdditionalLinksPanel.addAdditionalLinks(labelIfRegular, ID_ASSOCIATED_ACTION_LINKS_RIGHT, entityActionsRight, AdditionalLinksPanel.Style.DROPDOWN);
    }

    private static String determinePropParamLayoutCss(ScalarModel model) {
        final LabelAtFacet facet = model.getFacet(LabelAtFacet.class);
        if (facet != null) {
            switch (facet.label()) {
            case LEFT:
                return "label-left";
            case RIGHT:
                return "label-right";
            case NONE:
                return "label-none";
            case TOP:
                return "label-top";
            default:
                break;
            }
        }
        return "label-left";
    }

    private static String determineActionLayoutPositioningCss(List<LinkAndLabel> entityActionLinks) {
        boolean actionsPositionedOnRight = hasActionsPositionedOn(entityActionLinks, ActionLayout.Position.RIGHT);
        return actionsPositionedOnRight ? "actions-right" : null;
    }

    private static boolean hasActionsPositionedOn(final List<LinkAndLabel> entityActionLinks, final ActionLayout.Position position) {
        for (LinkAndLabel entityActionLink : entityActionLinks) {
            if(entityActionLink.getActionUiMetaModel().getPosition() == position) {
                return true;
            }
        }
        return false;
    }

    // //////////////////////////////////////

    private final List<ScalarModelSubscriberLegacy> subscribers = _Lists.newArrayList();

    public void notifyOnChange(final ScalarModelSubscriberLegacy subscriber) {
        subscribers.add(subscriber);
    }

    // //////////////////////////////////////

    /**
     * Optional hook method
     *
     * @return true - indicates has been updated, so update dynamically via ajax
     */
    public boolean updateChoices(ManagedObject[] pendingArguments) {
        return false;
    }

    /**
     * Repaints this panel of just some of its children
     *
     * @param target The Ajax request handler
     */
    public void repaint(AjaxRequestTarget target) {
        target.add(this);
    }

}