/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 -----------------------------------------------------------------------------*/
package com.ericsson.oss.services.cmsubscribedevents.exceptions;

import javax.ejb.ApplicationException;

/**
 * Application exception for use if a requested subscription to GET is unavailable.
 */
@ApplicationException(rollback = true)
public class SubscriptionNotFoundToGetException extends SubscriptionNotFoundException {

    private static final long serialVersionUID = -2418966955996722897L;

}
