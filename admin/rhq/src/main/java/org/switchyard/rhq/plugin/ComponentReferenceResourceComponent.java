/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.rhq.plugin;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.modules.plugins.jbossas7.json.Operation;
import org.switchyard.rhq.plugin.model.Application;
import org.switchyard.rhq.plugin.model.ComponentReference;
import org.switchyard.rhq.plugin.model.ComponentReferenceMetrics;
import org.switchyard.rhq.plugin.model.ComponentService;

/**
 * SwitchYard Component Reference Resource Component
 */
public class ComponentReferenceResourceComponent extends BaseSwitchYardResourceComponent<ComponentServiceResourceComponent> implements MeasurementFacet {
    /**
     * The logger instance.
     */
    private static Log LOG = LogFactory.getLog(ComponentReferenceResourceComponent.class);
    
    protected Log getLog() {
        return LOG;
    }

    @Override
    public AvailabilityType getAvailability() {
        final ComponentReference componentReference = getComponentReference();
        return (componentReference == null) ? AvailabilityType.DOWN : AvailabilityType.UP;
    }
    
    public ComponentReference getComponentReference() {
        final String referenceKey = getResourceContext().getResourceKey();
        return getComponentService().getReferences().get(referenceKey);
    }

    public ComponentService getComponentService() {
        return getResourceContext().getParentResourceComponent().getComponentService();
    }
    
    public Application getApplication() {
        return getResourceContext().getParentResourceComponent().getApplication();
    }

    private ComponentReferenceMetrics getComponentReferenceMetrics() {
        final String componentReferenceKey = getResourceContext().getResourceKey();
        return getResourceContext().getParentResourceComponent().getComponentReferenceMetrics().get(componentReferenceKey);
    }

    @Override
    public void getValues(final MeasurementReport report, final Set<MeasurementScheduleRequest> requests) throws Exception {
        final ComponentReferenceMetrics metrics = getComponentReferenceMetrics();
        if (metrics != null) {
            for (MeasurementScheduleRequest request: requests) {
                final MeasurementDataNumeric measurementData = getCommonMetric(request, metrics);
                if (measurementData != null) {
                    report.addData(measurementData);
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to collect Component Reference measurement " + request.getName());
                }
            }
        }
    }

    public <T> T execute(final Operation operation, Class<T> clazz) {
        return getResourceContext().getParentResourceComponent().execute(operation, clazz);
    }
}
