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

import static com.ericsson.oss.services.cmsubscribedevents.test.jee.Artifact.BEANS_XML_FILE;
import static com.ericsson.oss.services.cmsubscribedevents.test.jee.Artifact.MANIFEST_MF_FILE;
import static com.ericsson.oss.services.cmsubscribedevents.test.jee.Artifact.createModuleArchive;
import static com.ericsson.oss.services.cmsubscribedevents.test.jee.Artifact.getRequiredLibraries;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToGetException;
import java.io.IOException;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Arquillian test - Injecting EJB, Creating Archives and Libraries and test end-to-end functionalities
 */
@RunWith(Arquillian.class)
public final class SubscriptionPersistenceServiceIT {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionPersistenceServiceIT.class);

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(final Description description) {
            logger.info("*******************************");
            logger.info("Starting test: {}()", description.getMethodName());
        }

        @Override
        protected void finished(final Description description) {
            logger.info("Ending test: {}()", description.getMethodName());
            logger.info("*******************************");
        }
    };

    @Inject
    SubscriptionPersistenceServiceProxy subscriptionPersistenceServiceProxy;

    @Deployment(name = "CMSubscribedEventsSubscriptionManagerDeployment")

    public static Archive<?> createTestArchive() {
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "CMSubscribedEventsSubMgrTest.ear");
        ear.addAsLibrary(createModuleArchive());
        ear.addAsLibraries(getRequiredLibraries());
        ear.setManifest(MANIFEST_MF_FILE);
        ear.addAsApplicationResource(BEANS_XML_FILE);
        return ear;
    }

    @Test
    @InSequence(1)
    public void when_subscription_is_created_then_json_returned_with_id_replaced() throws IOException {
        final String createSubscriptionResponse = subscriptionPersistenceServiceProxy.getSubscriptionPersistenceService().createSubscription(
            "{\"ntfSubscriptionControl\":{\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"scope\":{\"scopeType\":\"BASE_ALL\"},\"id\":\"String_id\",\"objectClass\":\"/\",\"objectInstance\":\"/\"}}");
        logger.info("Created Subscription: {}", createSubscriptionResponse);
        assertFalse(createSubscriptionResponse.contains("String_id"));
    }

    @Test
    @InSequence(2)
    public void when_subscription_is_created_view_returns_json() throws IOException {
        final String expectedSubscriptionResponse = "{\"ntfSubscriptionControl\":{\"id\":\"1\",\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"objectInstance\":\"/\",\"objectClass\":\"/\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":0}}}";
        final String viewSubscriptionResponse = subscriptionPersistenceServiceProxy.getSubscriptionPersistenceService().viewSubscription(1);
        logger.info("View Subscription response: {}", viewSubscriptionResponse);

        assertEquals("View subscription not the expected response", expectedSubscriptionResponse, viewSubscriptionResponse);
    }

    @Test
    @InSequence(3)
    public void when_subscription_is_deleted_view_throws_exception() throws IOException {
        Exception exception = null;
        final String expectedSubscriptionResponse = "{\"ntfSubscriptionControl\":{\"id\":\"1\",\"notificationRecipientAddress\":\"https://site.com/eventListener/v10\",\"objectInstance\":\"/\",\"objectClass\":\"/\",\"scope\":{\"scopeType\":\"BASE_ALL\",\"scopeLevel\":0}}}";
        String viewSubscriptionResponse = subscriptionPersistenceServiceProxy.getSubscriptionPersistenceService().viewSubscription(1);
        assertEquals(expectedSubscriptionResponse, viewSubscriptionResponse);
        subscriptionPersistenceServiceProxy.getSubscriptionPersistenceService().deleteSubscription(1);

        try {
            subscriptionPersistenceServiceProxy.getSubscriptionPersistenceService().viewSubscription(1);
        } catch (final Exception thrownException) {
            exception = thrownException;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof SubscriptionNotFoundToGetException);
    }
}