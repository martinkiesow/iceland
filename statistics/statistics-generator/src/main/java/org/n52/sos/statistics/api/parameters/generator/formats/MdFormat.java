/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.statistics.api.parameters.generator.formats;

import java.util.List;
import java.util.Map;

import org.n52.iceland.statistics.api.parameters.AbstractEsParameter;
import org.n52.iceland.statistics.api.parameters.ObjectEsParameter;
import org.n52.iceland.statistics.api.parameters.SingleEsParameter;
import org.n52.iceland.statistics.api.parameters.Description.InformationOrigin;
import org.n52.iceland.statistics.api.parameters.Description.Operation;

import com.google.api.client.repackaged.com.google.common.base.Strings;

public class MdFormat {

    private Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> parameters;
    private StringBuilder output;
    private static final String LINE_TEMPLATE = " - **<fieldname>** - <desc>.";
    private static final String TYPE_TEMPLATE = " Type: <type>";
    private static final String NEW_LINE = "\n";
    private static final String NO_DESCRIPTION = "No available description";
    private static final String TAB = "\t";

    private String formatLine(AbstractEsParameter parameter,
            int indent) {
        String line = Strings.repeat(TAB, indent);

        // Fieldname
        line += LINE_TEMPLATE.replace("<fieldname>", parameter.getName());

        // Description
        if (parameter.getDescription() != null) {
            if (parameter.getDescription().getDesc() != null) {
                line = line.replace("<desc>", parameter.getDescription().getDesc());
            } else {
                line = line.replace("<desc>", NO_DESCRIPTION);
            }

            // Type
            if (parameter.getType() != null) {
                line += TYPE_TEMPLATE.replace("<type>", parameter.getType().humanReadableType());
            }
        } else {
            line = line.replace("<desc>", NO_DESCRIPTION);
        }

        line += NEW_LINE;
        return line;
    }

    private void format(ObjectEsParameter parameter,
            int indent) {
        output.append(formatLine(parameter, indent));
        parameter.getAllChildren().stream().forEach(l -> this.appendToOutput(l, indent + 1));
    }

    private void appendToOutput(AbstractEsParameter parameter,
            int indent) {
        if (parameter instanceof ObjectEsParameter) {
            format((ObjectEsParameter) parameter, indent);
        } else {
            format((SingleEsParameter) parameter, indent);
        }
    }

    private void format(SingleEsParameter parameter,
            int indent) {
        output.append(formatLine(parameter, indent));
    }

    private String formatH1(String line) {
        return NEW_LINE + "###" + line + NEW_LINE;
    }

    private String formatH2(String line) {
        return NEW_LINE + "####" + line + NEW_LINE;
    }

    public String create() {
        output = new StringBuilder();
        // Header 1
        for (Operation op : parameters.keySet()) {
            output.append(formatH1(op.toString()));
            // Header 2
            for (InformationOrigin origin : parameters.get(op).keySet()) {
                output.append(formatH2(origin.toString()));
                parameters.get(op).get(origin).stream().forEach(l -> appendToOutput(l, 0));
            }
        }
        return output.toString();
    }

    public Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<Operation, Map<InformationOrigin, List<AbstractEsParameter>>> parameters) {
        this.parameters = parameters;
    }

}
