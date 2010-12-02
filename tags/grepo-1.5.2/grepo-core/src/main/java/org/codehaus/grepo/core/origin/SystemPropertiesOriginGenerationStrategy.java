/*
 * Copyright 2010 Grepo Committers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.grepo.core.origin;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This strategy generates origin based on system properties.
 *
 * The properties which should be used are set in the {@link #properties} map. The values for
 * this map are used as default-values if the associated system property does not exist.
 *
 * Values are delimited by {@link #propertyDelimiter}.
 *
 * If no properties are set then {@link #defaultOrigin} is returned.
 *
 * @author dguggi
 */
public class SystemPropertiesOriginGenerationStrategy {
    /** The property delimiter. */
    private String propertyDelimiter = "-";

    /** The default origin. */
    private String defaultOrigin = "unknown";

    /** The property definitions. */
    private Map<String, String> propertyDefinitions;

    public String getPropertyDelimiter() {
        return propertyDelimiter;
    }

    public void setPropertyDelimiter(String propertyDelimiter) {
        this.propertyDelimiter = propertyDelimiter;
    }

    public String getDefaultOrigin() {
        return defaultOrigin;
    }

    public void setDefaultOrigin(String defaultOrigin) {
        this.defaultOrigin = defaultOrigin;
    }

    public Map<String,String> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    public void setPropertyDefinitions(Map<String,String> propertyDefinitions) {
        this.propertyDefinitions = propertyDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    public String generateOrigin() {
        StringBuilder origin = new StringBuilder();
        if (propertyDefinitions == null || propertyDefinitions.isEmpty()) {
            origin.append(defaultOrigin);
        } else {
            for (String key : propertyDefinitions.keySet()) {
                if (origin.length() > 0 && !StringUtils.isEmpty(propertyDelimiter)) {
                    origin.append(propertyDelimiter);
                }

                String value = System.getProperty(key, propertyDefinitions.get(key));
                origin.append(value);
            }
        }

        return origin.toString();
    }
}
