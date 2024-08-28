/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.cmsubscribedevents.test.jee;

import com.ericsson.oss.services.cmsubscribedevents.test.jee.ejb.SubscriptionPersistenceServiceIT;
import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * Contains methods for building test artifacts.
 */
public class Artifact {

    public static final File MANIFEST_MF_FILE = new File("src/test/resources/META-INF/MANIFEST.MF");

    public static final File BEANS_XML_FILE = new File("src/test/resources/META-INF/beans.xml");

    public static final String COM_ERICSSON_OSS_SERVICES_CMSUBSCRIBEDEVENTS = "com.ericsson.oss.services.cmsubscribedevents";

    public static final String CMSUBSCRIBEDEVENTSSUBSCRIPTIONMGR_API = "cmsubscribedeventssubscriptionmgr-api";

    public static File[] getRequiredLibraries() {
        return resolveAsFiles(COM_ERICSSON_OSS_SERVICES_CMSUBSCRIBEDEVENTS, CMSUBSCRIBEDEVENTSSUBSCRIPTIONMGR_API);
    }

    public static Archive<?> createModuleArchive() {
        return ShrinkWrap.create(JavaArchive.class, "cm-subscribed-event-nbi-test-bean-lib.jar")
            .addAsResource(MANIFEST_MF_FILE, "META-INF/MANIFEST.MF")
            .addAsResource("META-INF/beans.xml", "META-INF/beans.xml")
            .addPackage(SubscriptionPersistenceServiceIT.class.getPackage())
            .addPackage(Artifact.class.getPackage());
    }

    public static File[] resolveAsFiles(final String groupId, final String artifactId) {
        return Maven.resolver().loadPomFromFile("pom.xml").resolve(groupId + ":" + artifactId).withTransitivity().asFile();
    }

}