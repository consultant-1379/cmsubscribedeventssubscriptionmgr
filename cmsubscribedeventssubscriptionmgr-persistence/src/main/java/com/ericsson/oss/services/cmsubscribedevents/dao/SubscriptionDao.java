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
 -----------------------------------------------------------------------------*/
package com.ericsson.oss.services.cmsubscribedevents.dao;

import com.ericsson.oss.services.cmsubscribedevents.entities.NtfSubscriptionControl;
import com.ericsson.oss.services.cmsubscribedevents.entities.NtfSubscriptionControlWrapper;
import com.ericsson.oss.services.cmsubscribedevents.entities.Scope;
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToDeleteException;
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToGetException;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for saving an NtfSubscriptionControl entity through persistence.
 */
@Stateless
@LocalBean
public class SubscriptionDao {

    final Logger logger = LoggerFactory.getLogger(SubscriptionDao.class);

    @Inject
    private EntityManager entityManager;

    /**
     * Create Subscription entity
     *
     * @param ntfSubscriptionControlWrapper
     *     Contains NtfSubscriptionControl entity to be persisted
     */
    public int createSubscription(final NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper) {
        final Scope proposedScope = ntfSubscriptionControlWrapper.getNtfSubscriptionControl().getScope();
        if (proposedScope != null) {
            final Scope existingScope = scopeExistsInDb(proposedScope.getScopeType(), proposedScope.getScopeLevel());
            // If Scope details given already exist in DB, point Subscription to existing entry
            if (existingScope != null) {
                ntfSubscriptionControlWrapper.getNtfSubscriptionControl().setScope(existingScope);
            }
        }

        entityManager.persist(ntfSubscriptionControlWrapper.getNtfSubscriptionControl());
        entityManager.flush();
        return ntfSubscriptionControlWrapper.getNtfSubscriptionControl().getId();
    }

    /**
     * Fetches all the subscriptions and wraps it in a list of {@link NtfSubscriptionControlWrapper}.
     *
     * @return A list of {@link NtfSubscriptionControlWrapper} representing all the subscriptions.
     */
    public List<NtfSubscriptionControlWrapper> viewAllSubscriptions() {
        final TypedQuery<NtfSubscriptionControl> query = entityManager.createNamedQuery("NtfSubscriptionControl.findAll", NtfSubscriptionControl.class);
        List<NtfSubscriptionControlWrapper> subscriptions = new ArrayList<>();
        query.getResultList().forEach(ntfSubscriptionControl -> subscriptions.add(new NtfSubscriptionControlWrapper(ntfSubscriptionControl)));
        return subscriptions;
    }

    /**
     * Gets the ntfSubscriptionControl for the specified subscriptionId from the db and wraps it in NtfSubscriptionControlWrapper
     *
     * @param subscriptionId
     *     ID of subscription
     * @return NtfSubscriptionControlWrapper
     */
    public NtfSubscriptionControlWrapper viewSubscription(final Integer subscriptionId) {
        final NtfSubscriptionControl ntfSubscriptionControl = entityManager.find(NtfSubscriptionControl.class, subscriptionId);
        if (ntfSubscriptionControl == null) {
            throw new SubscriptionNotFoundToGetException();
        }
        return new NtfSubscriptionControlWrapper(ntfSubscriptionControl);
    }

    private Scope scopeExistsInDb(final String scopeType, final int scopeLevel) {
        final TypedQuery<Scope> query = entityManager.createNamedQuery("Scope.findDuplicateScopes", Scope.class).setParameter("scopeType", scopeType)
            .setParameter("scopeLevel", scopeLevel);
        final List<Scope> scopeEntitiesFound = query.getResultList();
        return scopeEntitiesFound.isEmpty() ? null : scopeEntitiesFound.get(0);
    }

    /**
     * Delete Subscription entity
     *
     * @param subscriptionId
     *    Contains subscriptionId for the NtfSubscriptionControl entity to be removed
     */
    public synchronized void deleteSubscription(final int subscriptionId)  {

        final NtfSubscriptionControl subscriptionToDelete = entityManager.find(NtfSubscriptionControl.class, subscriptionId);

        if (subscriptionToDelete != null) {

            final Scope scope = subscriptionToDelete.getScope();
            if (scope != null) {
                final TypedQuery<NtfSubscriptionControl> query = entityManager.createNamedQuery("NtfSubscriptionControl.findByScope", NtfSubscriptionControl.class).setParameter("scope", scope);
                final List<NtfSubscriptionControl> subscriptionsWithSameScope = query.getResultList();
                if (subscriptionsWithSameScope.size() == 1) {
                    entityManager.remove(scope);
                }
            }
            entityManager.remove(subscriptionToDelete);
            entityManager.flush();
        }
        else {
            throw new SubscriptionNotFoundToDeleteException();
        }
    }
}