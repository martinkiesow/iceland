/*
 * Copyright 2015-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.request.operator;

import java.util.Set;

import org.n52.janmayen.component.Component;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.svalbard.ConformanceClass;

/**
 * Interface for SOS request operator implementations
 * method
 *
 * @since 1.0.0
 */
public interface RequestOperator extends ConformanceClass, Component<RequestOperatorKey> {

    /**
     * Receives and processes the incoming {@link OwsServiceRequest} and
     * returns a {@link OwsServiceResponse}
     *
     * @param request
     *            The incoming {@link OwsServiceRequest}
     * @return {@link OwsServiceResponse} of the
     *         {@link OwsServiceRequest}
     * @throws OwsExceptionReport
     *             If an error occurs during the processing of the
     *             {@link OwsServiceRequest}
     */
    OwsServiceResponse receiveRequest(OwsServiceRequest request) throws OwsExceptionReport;

    /**
     * Get {@link OwsOperation} metadata for service and version
     *
     * @param service
     *            The service to get metadata for
     * @param version
     *            The service version to get metadata for
     * @return {@link OwsOperation} metadata for service and version
     * @throws OwsExceptionReport
     *             If an error occurs during the generation of
     *             {@link OwsOperation}
     */
    OwsOperation getOperationMetadata(String service, String version) throws OwsExceptionReport;

    @Deprecated
    default RequestOperatorKey getRequestOperatorKeyType() {
        Set<RequestOperatorKey> keys = getKeys();
        if (keys.size() <= 1) {
            return keys.stream().findAny().orElse(null);
        } else {
            throw new UnsupportedOperationException("This operator has multiple keys");
        }
    }
}
