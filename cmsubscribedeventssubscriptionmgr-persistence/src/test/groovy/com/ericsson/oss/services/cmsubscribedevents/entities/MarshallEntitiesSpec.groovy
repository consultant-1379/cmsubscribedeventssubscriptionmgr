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
package com.ericsson.oss.services.cmsubscribedevents.entities

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.fasterxml.jackson.databind.ObjectMapper

class MarshallEntitiesSpec extends CdiSpecification {

    def "when valid subscription string with scope, string is mapped to object"() {
        given: "valid json string for subscription"
            String subscription = "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":3},\"id\":\"9\",\"objectClass\":\"/\",\"objectInstance\":\"/\",\"notificationTypes\":[\"notifyMOICreation\",\"notifyMOIDeletion\"],\"notificationFilter\":\"filter\"}}"
        when: "string is mapped to an object"
            final ObjectMapper mapper = new ObjectMapper()
            final NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = mapper.readValue(subscription, NtfSubscriptionControlWrapper.class)
        then: "object is successfully created"
            NtfSubscriptionControl ntfSubscriptionControl = ntfSubscriptionControlWrapper.getNtfSubscriptionControl()
            ntfSubscriptionControl.getNotificationRecipientAddress() == "https://site.com/eventListener/v10"
            ntfSubscriptionControl.getNotificationTypes() == ["notifyMOICreation", "notifyMOIDeletion"] as String[]
            ntfSubscriptionControl.getObjectInstance() == "/"
            ntfSubscriptionControl.getObjectClass() == "/"
            ntfSubscriptionControl.getNotificationFilter() == "filter"
            ntfSubscriptionControl.getScope().getScopeLevel() == 3
            ntfSubscriptionControl.getScope().getScopeType() == "BASE_ALL"
    }

    def "when valid subscription string without scope and filters, string is mapped to object"() {
        given: "valid json string for subscription"
            String subscription = "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"id\":\"9\",\"objectClass\":\"/\",\"objectInstance\":\"/\"}}"
        when: "string is mapped to an object"
            final ObjectMapper mapper = new ObjectMapper()
            final NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = mapper.readValue(subscription, NtfSubscriptionControlWrapper.class)
        then: "object is successfully created"
            NtfSubscriptionControl ntfSubscriptionControl = ntfSubscriptionControlWrapper.getNtfSubscriptionControl()
            ntfSubscriptionControl.getNotificationRecipientAddress() == "https://site.com/eventListener/v10"
            ntfSubscriptionControl.getNotificationTypes() == [] as String[]
            ntfSubscriptionControl.getObjectInstance() == "/"
            ntfSubscriptionControl.getObjectClass() == "/"
            ntfSubscriptionControl.getNotificationFilter() == null
            ntfSubscriptionControl.getScope() == null
    }
}