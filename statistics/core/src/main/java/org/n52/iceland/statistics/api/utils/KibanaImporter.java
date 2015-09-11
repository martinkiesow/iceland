/*
 * Copyright 2015 52°North Initiative for Geospatial Open Source
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
package org.n52.iceland.statistics.api.utils;

import java.io.IOException;
import java.util.Objects;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.n52.iceland.statistics.api.utils.dto.KibanaConfigEntryDto;
import org.n52.iceland.statistics.api.utils.dto.KibanaConfigHolderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KibanaImporter {

    private static Logger logger = LoggerFactory.getLogger(KibanaImporter.class);

    public static final String INDEX_NEEDLE = "##!NO_SPOON!##";

    private final Client client;
    private final String kibanaIndexName;
    private final String statisticsIndexName;

    public KibanaImporter(Client client, String kibanaIndexName, String statisticsIndexName) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(kibanaIndexName);
        Objects.requireNonNull(statisticsIndexName);

        this.kibanaIndexName = kibanaIndexName;
        this.statisticsIndexName = statisticsIndexName;
        this.client = client;
    }

    public void importJson(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        Objects.requireNonNull(jsonString);

        // delete .kibana index
        try {
            client.admin().indices().prepareDelete(kibanaIndexName).get();
        } catch (ElasticsearchException ex) {
            logger.debug("Tried to delete kibana index " + kibanaIndexName + " but it is not exists", ex);
        }

        ObjectMapper mapper = new ObjectMapper();
        KibanaConfigHolderDto holder = mapper.readValue(jsonString, KibanaConfigHolderDto.class);

        for (KibanaConfigEntryDto dto : holder.getEntries()) {
            dto = processDto(dto);
            logger.debug("Importing {}", dto);
            client.prepareIndex(kibanaIndexName, dto.getType(), dto.getId()).setSource(dto.getSource()).setOperationThreaded(false).get();
        }
    }

    private KibanaConfigEntryDto processDto(KibanaConfigEntryDto dto) {
        if (dto.getType().equals("index-pattern")) {
            dto.setId(statisticsIndexName);
        }
        dto.setSource(dto.getSource().replaceAll(INDEX_NEEDLE, statisticsIndexName));
        return dto;
    }
}