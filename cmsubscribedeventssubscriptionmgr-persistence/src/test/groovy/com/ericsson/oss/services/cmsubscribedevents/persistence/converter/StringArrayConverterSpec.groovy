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
package com.ericsson.oss.services.cmsubscribedevents.persistence.converter

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class StringArrayConverterSpec extends CdiSpecification {

    StringArrayConverter stringArrayConverter = new StringArrayConverter()

    def "when string array with multiples entry, db column will contain entries separated by a comma"() {

        given: "valid string array"
            final String[] notificationTypes = new String[3]
            notificationTypes[0] = "notifyMOICreation"
            notificationTypes[1] = "notifyMOIDeletion"
            notificationTypes[2] = "notifyMOIAttributeValueChanges"

        when: "array converted to database column"
            final String columnValue = stringArrayConverter.convertToDatabaseColumn(notificationTypes)

        then: "array contents are joined, separated by a comma"
            columnValue == "notifyMOICreation,notifyMOIDeletion,notifyMOIAttributeValueChanges"
    }

    def "when string array with 0 entries, db column will be null"() {

        given: "valid string array"
            final String[] notificationTypes = new String[0]

        when: "array converted to database column"
            final String columnValue = stringArrayConverter.convertToDatabaseColumn(notificationTypes)

        then: "array contents are joined, separated by a comma"
            columnValue == null
    }

    def "when string array is null, db column will be null"() {

        given: "valid string array"
            final String[] notificationTypes = null

        when: "array converted to database column"
            final String columnValue = stringArrayConverter.convertToDatabaseColumn(notificationTypes)

        then: "array contents are joined, separated by a comma"
            columnValue == null
    }

    def "when db column with comma separated list, entity value is String array"() {

        given: "valid comma separated string"
            final String columnValue = "notifyMOICreation,notifyMOIDeletion,notifyMOIAttributeValueChanges"
            final String[] notificationTypes = new String[3]
            notificationTypes[0] = "notifyMOICreation"
            notificationTypes[1] = "notifyMOIDeletion"
            notificationTypes[2] = "notifyMOIAttributeValueChanges"

        when: "string converted to entity list"
            final String[] entityValue = stringArrayConverter.convertToEntityAttribute(columnValue)

        then: "entity contents is string array"
            entityValue == notificationTypes
    }

    def "when empty string in column, entity value is null"() {

        given: "valid string array"
            final String columnValue = ""

        when: "array converted to database column"
            final String[] entityValue = stringArrayConverter.convertToEntityAttribute(columnValue)

        then: "array contents are joined, separated by a comma"
            entityValue == new String[0]
    }

    def "when null column value, entity value will be an empty array"() {

        given: "valid string array"
            final String notificationTypes = null

        when: "array converted to database column"
            final String[] entityValue = stringArrayConverter.convertToEntityAttribute(notificationTypes)

        then: "array contents are joined, separated by a comma"
            entityValue == new String[0]
    }
}