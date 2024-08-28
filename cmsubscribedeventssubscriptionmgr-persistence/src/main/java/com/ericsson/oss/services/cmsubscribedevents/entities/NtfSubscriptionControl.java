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

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.ericsson.oss.services.cmsubscribedevents.persistence.converter.StringArrayConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * This entity class represents the data stored in postgres for a Subscription.
 */
@Entity
@Table(name = "cmsubscribedeventssubs")
@NamedQuery(name = "NtfSubscriptionControl.findAll", query = "SELECT s FROM NtfSubscriptionControl s")
@NamedQuery(name = "NtfSubscriptionControl.findBySubscriptionId", query = "SELECT s FROM NtfSubscriptionControl s WHERE s.id = :id")
@NamedQuery(name = "NtfSubscriptionControl.findByScope", query = "SELECT s FROM NtfSubscriptionControl s WHERE s.scope = :scope")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NtfSubscriptionControl {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @JsonSerialize(using = ToStringSerializer.class)
    private int id;

    @NotNull
    private String notificationRecipientAddress;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Convert(converter = StringArrayConverter.class)
    private String[] notificationTypes;

    @NotNull
    private String objectInstance;

    @NotNull
    private String objectClass;

    private String notificationFilter;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "scopeId", referencedColumnName = "id", nullable = true)
    private Scope scope;

    public NtfSubscriptionControl() {}

    public NtfSubscriptionControl(final int id,
            final String notificationRecipientAddress,
            final String[] notificationTypes,
            final String objectInstance,
            final String objectClass,
            final String notificationFilter,
            final Scope scope) {
        this.id = id;
        this.notificationRecipientAddress = notificationRecipientAddress;
        if (notificationTypes != null) {
            this.notificationTypes = notificationTypes.clone();
        }
        this.objectInstance = objectInstance;
        this.objectClass = objectClass;
        this.notificationFilter = notificationFilter;
        this.scope = scope;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getNotificationRecipientAddress() {
        return notificationRecipientAddress;
    }

    public void setNotificationRecipientAddress(final String notificationRecipientAddress) {
        this.notificationRecipientAddress = notificationRecipientAddress;
    }

    public String[] getNotificationTypes() {
        if (this.notificationTypes != null) {
            return this.notificationTypes.clone();
        }
        return new String[0];
    }

    public void setNotificationTypes(final String[] notificationTypes) {
        if (notificationTypes != null) {
            this.notificationTypes = notificationTypes.clone();
        } else {
            this.notificationTypes = null;
        }
    }

    public String getObjectInstance() {
        return objectInstance;
    }

    public void setObjectInstance(final String objectInstance) {
        this.objectInstance = objectInstance;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(final String objectClass) {
        this.objectClass = objectClass;
    }

    public String getNotificationFilter() {
        return notificationFilter;
    }

    public void setNotificationFilter(final String notificationFilter) {
        this.notificationFilter = notificationFilter;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(final Scope scope) {
        this.scope = scope;
    }
}