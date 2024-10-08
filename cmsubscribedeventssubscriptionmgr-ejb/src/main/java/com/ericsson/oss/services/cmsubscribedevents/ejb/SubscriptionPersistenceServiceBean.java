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
package com.ericsson.oss.services.cmsubscribedevents.ejb;

import static com.ericsson.oss.services.cmsubscribedevents.constants.InstrumentationConstants.CM_SUBSCRIBED_EVENTS_NBI_SUBSCRIPTION_CREATED;
import static com.ericsson.oss.services.cmsubscribedevents.constants.InstrumentationConstants.NOTIFICATION_TYPES;
import static com.ericsson.oss.services.cmsubscribedevents.constants.InstrumentationConstants.SCOPE;
import static com.ericsson.oss.services.cmsubscribedevents.constants.InstrumentationConstants.SUBSCRIPTION_ID;

import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.cmsubscribedevents.api.SubscriptionPersistenceService;
import com.ericsson.oss.services.cmsubscribedevents.dao.SubscriptionDao;
import com.ericsson.oss.services.cmsubscribedevents.entities.NtfSubscriptionControlWrapper;
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToDeleteException;
import com.ericsson.oss.services.cmsubscribedevents.exceptions.SubscriptionNotFoundToGetException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

/**
 * SubscriptionPersistenceServiceBean will interact with its DAO to perform CRUD operations on the cmsubscribedevents DB.
 */
@Stateless
public class SubscriptionPersistenceServiceBean implements SubscriptionPersistenceService {

    private static final String CM_SUBSCRIBED_EVENTS_NBI = "cm-subscribed-events-nbi";

    private static final String SUBSCRIPTIONS = "subscriptions";

    public static final String ID = "id";

    public static final String NTF_SUBSCRIPTION_CONTROL = "ntfSubscriptionControl";

    private final SubscriptionDao subscriptionDao;

    @Inject
    SystemRecorder systemRecorder;

    @Inject
    public SubscriptionPersistenceServiceBean(final SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    /**
     * Sends a JSON String representing a Subscription to be persisted
     *
     * @param subscriptionJsonString
     *     Json representation of the Subscription to be persisted
     * @return JSON string representing the newly created subscription with autogenerated subscription id
     * @throws IOException
     *     IOException can be thrown when executing database persistence
     *
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String createSubscription(final String subscriptionJsonString) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = mapper.readValue(updateSubscriptionId(subscriptionJsonString),
            NtfSubscriptionControlWrapper.class);
        final int subscriptionId = subscriptionDao.createSubscription(ntfSubscriptionControlWrapper);
        ntfSubscriptionControlWrapper.getNtfSubscriptionControl().setId(subscriptionId);
        systemRecorder.recordCommand("POST", CommandPhase.FINISHED_WITH_SUCCESS, CM_SUBSCRIBED_EVENTS_NBI, SUBSCRIPTIONS,
            "Subscription ID: " + subscriptionId);
        final Map<String, Object> data = new HashMap<>();
        data.put("MR", "105 65-0334/45191");
        systemRecorder.recordEventData("MR.EXECUTION", data);
        recordEventData(ntfSubscriptionControlWrapper);
        return mapper.writeValueAsString(ntfSubscriptionControlWrapper);
    }

    private String updateSubscriptionId(final String subscriptionJsonString) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(subscriptionJsonString);
        ((ObjectNode) jsonNode.path(NTF_SUBSCRIPTION_CONTROL)).put(ID, 0);
        return objectMapper.writeValueAsString(jsonNode);
    }

    private void recordEventData(final NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper) {
        final HashMap<String, Object> subscriptionDataMap = new HashMap<>();
        subscriptionDataMap.put(SUBSCRIPTION_ID, ntfSubscriptionControlWrapper.getNtfSubscriptionControl().getId());
        subscriptionDataMap.put(NOTIFICATION_TYPES, ntfSubscriptionControlWrapper.getNtfSubscriptionControl().getNotificationTypes());
        subscriptionDataMap.put(SCOPE, null == ntfSubscriptionControlWrapper.getNtfSubscriptionControl().getScope() ? " " : ntfSubscriptionControlWrapper.getNtfSubscriptionControl().getScope().getScopeType());
        systemRecorder.recordEventData(CM_SUBSCRIBED_EVENTS_NBI_SUBSCRIPTION_CREATED, subscriptionDataMap);
    }

    /**
     * Retrieves all the subscriptions and returns the data as a JSON String.
     *
     * @return All the subscriptions as a JSON String.
     * @throws JsonProcessingException
     *     Throws an JsonProcessingException if the view all subscriptions returns an object that cannot be converted to a JSON String.
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String viewAllSubscriptions() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final List<NtfSubscriptionControlWrapper> ntfSubscriptionControlWrappers = subscriptionDao.viewAllSubscriptions();
        return mapper.writeValueAsString(ntfSubscriptionControlWrappers);
    }

    /**
     * Returns a JSON String of the requested Subscription ID
     *
     * @param subscriptionID
     *     The Subscription ID
     * @return String the requested subscription in JSON format
     * @throws JsonProcessingException
     *     Throws an JsonProcessingException if the view subscription returns an object that cannot be converted to a JSON String
     * @throws SubscriptionNotFoundToGetException
     *     Exception thrown if a subscription is not found
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String viewSubscription(final Integer subscriptionID) throws JsonProcessingException, SubscriptionNotFoundToGetException {
        final ObjectMapper mapper = new ObjectMapper();
        final NtfSubscriptionControlWrapper ntfSubscriptionControlWrapper = subscriptionDao.viewSubscription(subscriptionID);
        return mapper.writeValueAsString(ntfSubscriptionControlWrapper);
    }

    /**
     * Deletes subscription with matching subscription ID
     *
     * @param subscriptionId
     *     The Subscription ID
     * @throws SubscriptionNotFoundToDeleteException
     *     Exception thrown if a subscription is not found
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteSubscription(final Integer subscriptionId) throws SubscriptionNotFoundToDeleteException {
        subscriptionDao.deleteSubscription(subscriptionId);
    }
}