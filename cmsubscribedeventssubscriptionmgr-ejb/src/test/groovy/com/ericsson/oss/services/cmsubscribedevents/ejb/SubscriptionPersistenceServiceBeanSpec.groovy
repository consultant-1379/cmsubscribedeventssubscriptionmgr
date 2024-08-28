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
package com.ericsson.oss.services.cmsubscribedevents.ejb

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.recording.CommandPhase
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder
import com.ericsson.oss.services.cmsubscribedevents.entities.NtfSubscriptionControl
import com.ericsson.oss.services.cmsubscribedevents.entities.Scope
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToDeleteException

import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

class SubscriptionPersistenceServiceBeanSpec extends CdiSpecification {

    @ImplementationInstance
    private EntityManager entityManager = [
            persist           : { NtfSubscriptionControl ntfSubscriptionControl ->
                ntfSubscriptionControl.setId(77)
            },
            merge             : {
                t -> return t
            },
            find              : { Class type, Integer id ->
                return new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", null, "/", "/", null, new Scope(2, "BASE_ALL", 0))
            },
            remove            : { NtfSubscriptionControl ntfSubscriptionControl -> null
            },
            createNamedQuery  : { String queryName, Class type ->
                return typedQueryMock
            },
            createQuery       : {
                return typedQueryMock
            },
            getCriteriaBuilder: {
                return criteriaBuilderMock
            },
            flush             : {}
    ] as EntityManager

    @ImplementationInstance
    private TypedQuery typedQueryMock = [
            getResultList: {
                list
            },
            setParameter : { String arg0, Object arg1 ->
                return typedQueryMock
            }
    ] as TypedQuery

    List<Object> list = new ArrayList<>()

    @Inject
    SystemRecorder systemRecorder

    @Inject
    SubscriptionPersistenceServiceBean subscriptionServiceBean

    String createdSubscriptionID = "77"

    private static final String CM_SUBSCRIBED_EVENTS_NBI = "cm-subscribed-events-nbi"

    private static final String SUBSCRIPTIONS = "subscriptions"

    def "when create subscription is successful json object is returned with updated id"() {
        given: "subscription json string"
            String subscription = "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":3},\"id\":\"subscriberId\",\"objectClass\":\"/\",\"objectInstance\":\"/\"}}"
        when: "create subscription"
            String persistedObject = subscriptionServiceBean.createSubscription(subscription)
        then: "returned json object has updated id"
            persistedObject.contains(createdSubscriptionID)
        and: "Error is logged to log viewer"
            1 * systemRecorder.recordCommand("POST", CommandPhase.FINISHED_WITH_SUCCESS, CM_SUBSCRIBED_EVENTS_NBI, SUBSCRIPTIONS,
                    "Subscription ID: " + createdSubscriptionID)
    }

    def "when create subscription with json object not having id, create is successful and returned with updated id"() {
        given: "subscription json string"
            String subscription = "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":3},\"objectClass\":\"/\",\"objectInstance\":\"/\"}}"
        when: "create subscription"
            String persistedObject = subscriptionServiceBean.createSubscription(subscription)
        then: "returned json object has updated id"
            persistedObject.contains(createdSubscriptionID)
        and: "Successful subscription creation is logged to log viewer"
            1 * systemRecorder.recordCommand("POST", CommandPhase.FINISHED_WITH_SUCCESS, CM_SUBSCRIBED_EVENTS_NBI, SUBSCRIPTIONS,
                    "Subscription ID: " + createdSubscriptionID)
    }

    def "when create subscription with json object not having 'scope' parameter, create is successful and returned with updated id"() {
        given: "subscription json string without 'scope' parameter"
            String subscription = "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"objectClass\":\"/\",\"objectInstance\":\"/\"}}"
        when: "create subscription"
            String persistedObject = subscriptionServiceBean.createSubscription(subscription)
        then: "returned json object has updated id"
            persistedObject.contains(createdSubscriptionID)
        and: "Successful subscription creation is logged to log viewer"
            1 * systemRecorder.recordCommand("POST", CommandPhase.FINISHED_WITH_SUCCESS, CM_SUBSCRIBED_EVENTS_NBI, SUBSCRIPTIONS,
                    "Subscription ID: " + createdSubscriptionID)
    }

    def "when create subscription with invalid json string, then IOException is thrown"() {
        given: "Invalid subscription json string"
            String subscription = "ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":3},\"objectClass\":\"/\",\"objectInstance\":\"/\"}}"
        when: "create subscription"
            subscriptionServiceBean.createSubscription(subscription)
        then: "IOException is thrown"
            thrown IOException
    }

    def "when create subscription with scope already persisted, the subscription is persist is successful"() {
        given: "subscription json string, and scope already existing"
            String subscription = "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":3},\"id\":\"9\",\"objectClass\":\"/\",\"objectInstance\":\"/\"}}"
            list.add(new Scope(2, "BASE_ALL", 3))
        when: "create subscription"
            String persistedObject = subscriptionServiceBean.createSubscription(subscription)
        then: "returned json object has updated id"
            persistedObject.contains(createdSubscriptionID)
            1 * systemRecorder.recordCommand("POST", CommandPhase.FINISHED_WITH_SUCCESS, CM_SUBSCRIBED_EVENTS_NBI, SUBSCRIPTIONS,
                    "Subscription ID: " + createdSubscriptionID)
    }

    def "when view all subscriptions is successful, json object containing all the subscriptions is returned"() {
        given: "Multiple subscriptions exists"
            list.add(new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", Arrays.asList("notifyMOICreation") as String[], "/", "/", "//MeContext", new Scope(1, "BASE_ALL", 0)))
            list.add(new NtfSubscriptionControl(3, "https://site.com/eventListener/v10", Arrays.asList("notifyMOIChanges") as String[], "/", "/", "//MeContext", new Scope(1, "BASE_ALL", 0)))
        when: "view all subscriptions is called"
            String persistedObject = subscriptionServiceBean.viewAllSubscriptions()
        then: "All subscriptions are returned"
            persistedObject.contains('[{"ntfSubscriptionControl":{"id":"1","notificationRecipientAddress":"https://site.com/eventListener/v10","notificationTypes":["notifyMOICreation"],"objectInstance":"/","objectClass":"/","notificationFilter":"//MeContext","scope":{"scopeType":"BASE_ALL","scopeLevel":0}}},' +
                    '{"ntfSubscriptionControl":{"id":"3","notificationRecipientAddress":"https://site.com/eventListener/v10","notificationTypes":["notifyMOIChanges"],"objectInstance":"/","objectClass":"/","notificationFilter":"//MeContext","scope":{"scopeType":"BASE_ALL","scopeLevel":0}}}]')
    }

    def "when view subscription is successful json object is returned"() {
        given: "subscription exists"
        when: "view subscription with ID is called"
            String persistedObject = subscriptionServiceBean.viewSubscription(77)
        then: "subscription is returned"
            persistedObject.contains('{"ntfSubscriptionControl":{"id":"1","notificationRecipientAddress":"https://site.com/eventListener/v10","objectInstance":"/","objectClass":"/","scope":{"scopeType":"BASE_ALL","scopeLevel":0}}}')
    }

    def "when delete subscription is successful, subscription is deleted"() {
        given: "subscription exists"
            subscriptionServiceBean.viewSubscription(77)
        when: "delete subscription with ID is called"
            subscriptionServiceBean.deleteSubscription(77)
        then: "SubscriptionNotFoundToDeleteException is not thrown"
            notThrown(SubscriptionNotFoundToDeleteException)
    }
}