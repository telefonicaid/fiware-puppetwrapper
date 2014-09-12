/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.pupperwrapper.services.integration.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;
import com.telefonica.euro_iaas.sdc.pupperwrapper.services.tests.CatalogManagerMongoImpl4Test;
import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;


@Ignore
public class CatalogManagerMongoIntegrationTest {


    private static CatalogManagerMongoImpl4Test catalogManagerMongo;

    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "itest";
    //    private static final int MONGO_TEST_PORT = 12345;
    private static MongodProcess mongoProcess;
    private static Mongo mongo;

    private MongoTemplate template;

    @BeforeClass
    public static void initializeDB() throws IOException {


        RuntimeConfig config = new RuntimeConfig();
        config.setExecutableNaming(new UserTempNaming());

        MongodStarter starter = MongodStarter.getInstance(config);

        int port = Network.getFreeServerPort();

        MongodExecutable mongoExecutable = starter.prepare(new MongodConfig(Version.V2_0_5, port, false));
        mongoProcess = mongoExecutable.start();

        mongo = new Mongo(LOCALHOST, port);
        mongo.getDB(DB_NAME);

    }

    @AfterClass
    public static void shutdownDB() throws InterruptedException {
        mongo.close();
        mongoProcess.stop();
    }


    @Before
    public void setUp() throws Exception {
        catalogManagerMongo = new CatalogManagerMongoImpl4Test();
        template = new MongoTemplate(mongo, DB_NAME);
        catalogManagerMongo.setMongoTemplate(template);
    }

    @After
    public void tearDown() throws Exception {
        template.dropCollection(Node.class);
    }

    @Test(expected = NoSuchElementException.class)
    public void getNodeTestNotfound() {
        Node node = catalogManagerMongo.getNode("test");

    }

    @Test
    public void getNodeTest() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        Node node1 = catalogManagerMongo.getNode("test");
        assertTrue(node1.getId().equals("test"));
    }

    @Test
    public void testAddNode() {
        int length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 0);
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 1);
    }

    @Test
    public void testRemoveNode() {
        int length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 0);
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 1);

        catalogManagerMongo.removeNode(node.getId());
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 0);
    }

    @Test
    public void generateFileStrTestOnlyNode() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);

        String str = catalogManagerMongo.generateManifestStr("test");
        assertTrue(str.length() > 0);
        assertTrue(str.contains("{"));
        assertTrue(str.contains("node"));
    }

    @Test
    public void generateFileStrTestNodeAndSoft() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Software soft = new Software();
        soft.setName("testSoft");
        soft.setVersion("1.0.0");
        soft.setAction(Action.INSTALL);

        node.addSoftware(soft);

        catalogManagerMongo.addNode(node);

        String str = catalogManagerMongo.generateManifestStr("test");
        assertTrue(str.length() > 0);
        assertTrue(str.contains("{"));
        assertTrue(str.contains("node"));
        assertTrue(str.contains("class"));
        assertTrue(str.contains("install"));
        assertTrue(str.contains("version"));
    }

    @Test
    public void generateSiteFile() {
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Node node2 = new Node();
        node2.setId("test2");
        node2.setGroupName("group2");

        catalogManagerMongo.addNode(node);
        catalogManagerMongo.addNode(node2);

        String str = catalogManagerMongo.generateSiteStr();

        assertTrue(str.length() > 0);
        assertTrue(str.contains("import 'group/*.pp'"));
        assertTrue(str.contains("import 'group2/*.pp'"));
    }

    @Test
    public void removeNodesByGroupNameTest() {

        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Node node2 = new Node();
        node2.setId("test2");
        node2.setGroupName("group");

        catalogManagerMongo.addNode(node);
        catalogManagerMongo.addNode(node2);

        assertTrue(catalogManagerMongo.getNodeLength() == 2);

        catalogManagerMongo.removeNodesByGroupName("group");

        assertTrue(catalogManagerMongo.getNodeLength() == 0);

    }
}
