/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.cmsubscribedevents.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * AttributeConverter class to convert String[] in entity class to String in Entity column in DB and vice versa.
 *
 */
@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(final String[] arg0) {
        if (arg0 != null) {
            if (arg0.length == 0) {
                return null;
            }
            return String.join(",", arg0);
        }
        return null;
    }

    @Override
    public String[] convertToEntityAttribute(final String arg0) {
        if (arg0 != null) {
            if (arg0.isEmpty()) {
                return new String[0];
            }
            return arg0.split(",");
        }
        return new String[0];
    }
}
