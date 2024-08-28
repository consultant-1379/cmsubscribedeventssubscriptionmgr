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
package com.ericsson.oss.services.cmsubscribedevents.api;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToDeleteException;
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToGetException;
import java.io.IOException;
import javax.ejb.Remote;

/**
 * SubscriptionPersistenceService defines the API required to save, view or delete Subscription data in persistence.
 */
@EService
@Remote
public interface SubscriptionPersistenceService {

    /**
     * Saves a Subscription to persistence.
     *
     * @param subscriptionJsonString
     *     Json representation of the Subscription to be persisted
     * @return String of the created subscription in JSON format
     * @throws IOException
     *     Throws an IO Exception
     */
    String createSubscription(String subscriptionJsonString) throws IOException;

    /**
     * View all the subscriptions.
     *
     * @return String of the requested subscriptions in JSON format.
     * @throws IOException
     *     Throws an IO Exception
     */
    String viewAllSubscriptions() throws IOException;

    /**
     * View a Subscription.
     *
     * @param subscriptionID
     *     Subscription subscriptionID
     * @return String of the requested subscription in JSON format
     * @throws IOException
     *     Throws an IO Exception
     * @throws SubscriptionNotFoundToGetException
     *     Exception thrown if a subscription is not found
     */
    String viewSubscription(Integer subscriptionID) throws IOException, SubscriptionNotFoundToGetException;

    /**
     * Delete a Subscription.
     *
     * @param subscriptionID
     *     Subscription subscriptionID
     * @throws SubscriptionNotFoundToDeleteException
     *     Exception thrown if a subscription is not found
     */
    void deleteSubscription(Integer subscriptionID) throws SubscriptionNotFoundToDeleteException;

}