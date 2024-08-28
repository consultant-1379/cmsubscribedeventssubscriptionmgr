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
package com.ericsson.oss.services.cmsubscribedevents.entities;

/**
 * This entity class represents the top level NtfSubscriptionControl posted, which includes the {@link NtfSubscriptionControl}
 * to be persisted to postgres.
 */
public class NtfSubscriptionControlWrapper {
    private NtfSubscriptionControl ntfSubscriptionControl;

    public NtfSubscriptionControlWrapper() {}

    public NtfSubscriptionControlWrapper(final NtfSubscriptionControl ntfSubscriptionControl) {
        this.ntfSubscriptionControl = ntfSubscriptionControl;
    }
    public NtfSubscriptionControl getNtfSubscriptionControl() {
        return ntfSubscriptionControl;
    }

    public void setNtfSubscriptionControl(final NtfSubscriptionControl ntfSubscriptionControl) {
        this.ntfSubscriptionControl = ntfSubscriptionControl;
    }
}
