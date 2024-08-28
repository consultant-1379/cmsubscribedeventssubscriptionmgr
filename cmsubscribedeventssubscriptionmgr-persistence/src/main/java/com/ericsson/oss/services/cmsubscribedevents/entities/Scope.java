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


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity class represents the data stored in postgres for the Scope of a given Subscription.
 */
@Entity
@Table(name = "scope")
@NamedQuery(name = "Scope.findDuplicateScopes", query = "SELECT s FROM Scope s WHERE s.scopeType = :scopeType and scopeLevel = :scopeLevel")
public class Scope {
    @Id
    @NotNull
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonIgnore
    private int id;

    @NotNull
    private String scopeType;

    public Scope(final int id, final String scopeType, final int scopeLevel) {
        this.id = id;
        this.scopeType = scopeType;
        this.scopeLevel = scopeLevel;
    }

    private int scopeLevel;

    public Scope() {}

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Scope(final String scopeType) {
        this.scopeType = scopeType;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(final String scopeType) {
        this.scopeType = scopeType;
    }

    public int getScopeLevel() {
        return scopeLevel;
    }

    public void setScopeLevel(final int scopeLevel) {
        this.scopeLevel = scopeLevel;
    }
}
