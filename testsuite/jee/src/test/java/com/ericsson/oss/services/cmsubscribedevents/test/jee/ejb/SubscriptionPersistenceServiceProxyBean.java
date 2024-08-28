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
package com.ericsson.oss.services.cmsubscribedevents.test.jee.ejb;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cmsubscribedevents.api.SubscriptionPersistenceService;

/**
 * Implementation of a proxy of subscription persistence service to retrieve an instance of Subscription Persistence service for use with Arquillian
 * tests
 */
public class SubscriptionPersistenceServiceProxyBean implements SubscriptionPersistenceServiceProxy {

    @EServiceRef
    SubscriptionPersistenceService subscriptionPersistenceService;

    @Override
    public SubscriptionPersistenceService getSubscriptionPersistenceService() {
        return subscriptionPersistenceService;
    }
}
