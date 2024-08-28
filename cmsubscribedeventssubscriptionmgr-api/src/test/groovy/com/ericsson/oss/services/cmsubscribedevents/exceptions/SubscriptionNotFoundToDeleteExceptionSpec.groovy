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
 *  -----------------------------------------------------------------------------
 */
package com.ericsson.oss.services.cmsubscribedevents.exceptions

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import spock.lang.Specification

class SubscriptionNotFoundToDeleteExceptionSpec extends Specification {

    @ObjectUnderTest
    SubscriptionNotFoundToDeleteException subscriptionNotFoundToDeleteException

    def "Retrieve error message output from SubscriptionNotFoundToDeleteException"() {
        given: "A SubscriptionNotFoundToDeleteException is created"
            subscriptionNotFoundToDeleteException = new SubscriptionNotFoundToDeleteException()
        when: "When a calling class reads the message"
            def message = subscriptionNotFoundToDeleteException.getMessage()
        then: "The exception message is reported"
            message == "Subscription Not Found"
    }
}
