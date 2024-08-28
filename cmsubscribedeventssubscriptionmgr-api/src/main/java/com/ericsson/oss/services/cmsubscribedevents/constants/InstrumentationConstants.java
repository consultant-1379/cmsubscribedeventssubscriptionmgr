/*
 * ------------------------------------------------------------------------------
 *  *******************************************************************************
 *  * COPYRIGHT Ericsson 2023
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  *******************************************************************************
 *  *----------------------------------------------------------------------------
 */

package com.ericsson.oss.services.cmsubscribedevents.constants;

/**
 * Instrumentation related constants.
 */
public final class InstrumentationConstants {

    public static final String CM_SUBSCRIBED_EVENTS_NBI_SUBSCRIPTION_CREATED = "CM_SUBSCRIBED_EVENTS_NBI.SUBSCRIPTION_CREATED";

    public static final String CM_SUBSCRIBED_EVENTS_NBI_SUBSCRIPTION_DELETED = "CM_SUBSCRIBED_EVENTS_NBI.SUBSCRIPTION_DELETED";

    public static final String NOTIFICATION_TYPES = "notificationTypes";

    public static final String SCOPE = "scope";

    public static final String CM_SUBSCRIBED_EVENTS_NBI_SUBSCRIPTION = "CM_SUBSCRIBED_EVENTS_NBI.SUBSCRIPTION";

    public static final String SUBSCRIPTION_ID = "SubscriptionId";

    private InstrumentationConstants() {
        throw new IllegalStateException("Instrumentation constants utility class");
    }

}