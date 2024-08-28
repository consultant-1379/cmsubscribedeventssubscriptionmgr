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
package com.ericsson.oss.services.cmsubscribedevents.dao

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.cmsubscribedevents.entities.NtfSubscriptionControl
import com.ericsson.oss.services.cmsubscribedevents.entities.NtfSubscriptionControlWrapper
import com.ericsson.oss.services.cmsubscribedevents.entities.Scope
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToDeleteException
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToGetException
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

class SubscriptionDaoSpec extends CdiSpecification {

    List<Scope> list = new ArrayList<Scope>()

    @Inject
    SubscriptionDao subscriptionDao

    @Inject
    EntityManager entityManager

    @Inject
    TypedQuery typedQuery

    def setup() {
        list.clear()
    }

    def "when create subscription is successful json object is returned with updated id"() {
        given: "subscription object"
            NtfSubscriptionControl ntfSubscriptionControl = new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", null, "/", "/", null, null)
            NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = new NtfSubscriptionControlWrapper(ntfSubscriptionControl)
            entityManager.persist(ntfSubscriptionControl) >> ntfSubscriptionControl.setId(77)
        when: "create subscription"
            final String persistedSubscriptionId = subscriptionDao.createSubscription(ntfSubscriptionControlWrapper)
        then: "returned json object has updated id"
            persistedSubscriptionId == "77"
    }

    def "when create subscription with scope already persisted, the subscription is persisted is successful"() {
        given: "subscription json string, and scope already existing"
            Scope scope = new Scope(2, "BASE_ALL", 3)
            NtfSubscriptionControl ntfSubscriptionControl = new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", null, "/", "/", null, scope)
            NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = new NtfSubscriptionControlWrapper()
            ntfSubscriptionControlWrapper.setNtfSubscriptionControl(ntfSubscriptionControl)

            entityManager.persist(ntfSubscriptionControl) >> ntfSubscriptionControl.setId(77)
            entityManager.createNamedQuery("Scope.findDuplicateScopes", Scope.class) >> typedQuery
            typedQuery.setParameter("scopeType", "BASE_ALL") >> typedQuery
            typedQuery.setParameter("scopeLevel", 3) >> typedQuery
            list.add(scope)
            typedQuery.getResultList() >> list

        when: "create subscription"
            final String persistedSubscriptionId = subscriptionDao.createSubscription(ntfSubscriptionControlWrapper)
        then: "returned json object has updated id"
            persistedSubscriptionId == "77"
    }

    def "when view subscription is successful json object is returned"() {
        given: "subscription already exists"
            NtfSubscriptionControl ntfSubscriptionControlStoredInDB = new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", null, "/", "/", null, null)
            entityManager.find(NtfSubscriptionControl.class, 1) >> ntfSubscriptionControlStoredInDB

        when: "view subscription with subscriptionID is executed"
            NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = subscriptionDao.viewSubscription(1)
            NtfSubscriptionControl ntfSubscriptionControl = ntfSubscriptionControlWrapper.getNtfSubscriptionControl()

        then: "returned subscription for specified subscriptionID"
            ntfSubscriptionControl.getId() == 1
            ntfSubscriptionControl.getNotificationRecipientAddress() == 'https://site.com/eventListener/v10'
            ntfSubscriptionControl.getObjectClass() == '/'
            ntfSubscriptionControl.getObjectInstance() == '/'
    }

    def "when view subscription is unsuccessful error is returned"() {
        given: "subscription does not exist"
            entityManager.find(NtfSubscriptionControl.class, _) >> null
        when: "view subscription is executed"
            subscriptionDao.viewSubscription(1)
        then: "exception is thrown"
            thrown(SubscriptionNotFoundToGetException)
    }


    def "when delete subscription and no other subscription contains the same scope, then scope is deleted also"() {
        given: "subscription with scope already existing"
            Scope scope = new Scope(2, "BASE_ALL", 3)
            NtfSubscriptionControl ntfSubscriptionControl = new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", null, "/", "/", null, scope)
            entityManager.find(NtfSubscriptionControl.class, 1)

        when: "Delete is called"
            final int id = ntfSubscriptionControl.getId()
            entityManager.createNamedQuery("NtfSubscriptionControl.findByScope", NtfSubscriptionControl.class) >> typedQuery
            typedQuery.setParameter("scope", scope) >> typedQuery
            List<NtfSubscriptionControl> subs = new ArrayList<NtfSubscriptionControl>()
            subs.add(ntfSubscriptionControl)
            typedQuery.getResultList() >> subs

            entityManager.find(NtfSubscriptionControl.class, _) >> ntfSubscriptionControl
            subscriptionDao.deleteSubscription(id)
        then: "Both scope and subscription is removed"
            1 * entityManager.remove(scope)
            1 * entityManager.remove(ntfSubscriptionControl)

    }


    def "when delete subscription and scope is used by other subscription, then scope is not deleted"() {
        given: "subscription exists, but scope is referenced by another subscription"
            Scope scope = new Scope(2, "BASE_ALL", 3)
            NtfSubscriptionControl ntfSubscriptionControl = new NtfSubscriptionControl(1, "https://site.com/eventListener/v10", null, "/", "/", null, scope)
            NtfSubscriptionControl ntfSubscriptionControl2 = new NtfSubscriptionControl(3, "https://site.com/eventListener/v10", null, "/", "/", null, scope)
            entityManager.find(NtfSubscriptionControl.class, 1)
            entityManager.find(NtfSubscriptionControl.class, 3)

        when: "Delete is called"
            final int id = ntfSubscriptionControl.getId()
            entityManager.createNamedQuery("NtfSubscriptionControl.findByScope", NtfSubscriptionControl.class) >> typedQuery
            typedQuery.setParameter("scope", scope) >> typedQuery
            List<NtfSubscriptionControl> subs = new ArrayList<NtfSubscriptionControl>()
            subs.add(ntfSubscriptionControl)
            subs.add(ntfSubscriptionControl2)
            typedQuery.getResultList() >> subs

            entityManager.find(NtfSubscriptionControl.class, _) >> ntfSubscriptionControl
            subscriptionDao.deleteSubscription(id)
        then: "Subscription is removed and scope is not"
            0 * entityManager.remove(scope)
            1 * entityManager.remove(ntfSubscriptionControl)
    }

    def "When delete is called on a subscription that does not exists"() {
        given: "Subscription does not exists"
        when: "delete is called"
            subscriptionDao.deleteSubscription(3)
        then: "Subscription not found exception is thrown"
            thrown(SubscriptionNotFoundToDeleteException)
    }
}