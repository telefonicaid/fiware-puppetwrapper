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

package com.telefonica.euro_iaas.sdc.pupperwrapper.services.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;

public class CatalogManagerMongoTest {

    private CatalogManagerMongoImpl4Test catalogManagerMongo;

    private MongoTemplate mongoTemplateMock;

    @Before
    public void setUp() {

        mongoTemplateMock = mock(MongoTemplate.class);

        catalogManagerMongo = new CatalogManagerMongoImpl4Test();
        catalogManagerMongo.setMongoTemplate(mongoTemplateMock);

        final Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        node.setManifestGenerated(true);

        Software soft = new Software();
        soft.setName("test");
        soft.setVersion("1.0.0");
        soft.setAction(Action.INSTALL);
        node.addSoftware(soft);

        final Node node2 = new Node();
        node2.setId("test2");
        node2.setGroupName("group2");
        node2.setManifestGenerated(true);

        final Node node3 = new Node();
        node3.setId("test3");
        node3.setGroupName("group2");
        node3.setManifestGenerated(true);

//        Query query = mock(Query.class);
        Query query = new Query(Criteria.where("id").is("test"));
        when(mongoTemplateMock.findOne(query, Node.class)).thenReturn(node);

        query = new Query(Criteria.where("id").is("test3"));
        when(mongoTemplateMock.findOne(query, Node.class)).thenThrow(new NoSuchElementException());

        when(mongoTemplateMock.findAll(Node.class)).thenReturn(new ArrayList<Node>() {
            {
                add(node);
                add(node2);
                add(node3);
            }
        }).thenReturn(new ArrayList<Node>() {
            {
                add(node);
                add(node2);
                add(node3);
            }
        }).thenReturn(new ArrayList<Node>() {
            {
                add(node);
                add(node2);
            }
        });

        Query queryFindOK = new Query(Criteria.where("groupName").is("groupNameOK"));
        when(mongoTemplateMock.find(queryFindOK, Node.class)).thenReturn(new ArrayList<Node>() {
            {
                add(node);
            }
        });

        Query queryFindErr = new Query(Criteria.where("groupName").is("groupNameErr"));
        when(mongoTemplateMock.find(queryFindErr, Node.class)).thenReturn(null);

    }

    @Test(expected = NoSuchElementException.class)
    public void getNodeTest_notfound() {
        Node node = catalogManagerMongo.getNode("test3");

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
        assertTrue(length == 3);
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 3);
    }

    @Test
    public void testRemoveNode() {
        int length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 3);
        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");
        catalogManagerMongo.addNode(node);
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 3);

        catalogManagerMongo.removeNode(node.getId());
        length = catalogManagerMongo.getNodeLength();
        assertTrue(length == 2);
    }

    @Test
    public void generateFileStrTest_onlyNode() {
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
    public void generateFileStrTest_nodeAndSoft() {


        String str = catalogManagerMongo.generateManifestStr("test");
        assertTrue(str.length() > 0);
        assertTrue(str.contains("{"));
        assertTrue(str.contains("node"));
        assertTrue(str.contains("class"));
        assertTrue(str.contains("install"));
        assertTrue(str.contains("version"));
    }

    @Test
    public void generateSiteFileTest() {

        String str = catalogManagerMongo.generateSiteStr();

        assertTrue(str.length() > 1);
        assertTrue(str.contains("import 'group/*.pp'"));
        assertTrue(str.contains("import 'group2/*.pp'"));

    }

    @Test
    public void removeNodesByGroupNameTest() {

        catalogManagerMongo.removeNodesByGroupName("group");

        assertTrue(catalogManagerMongo.getNodeLength() == 3);

    }

    @Test
    public void isLastGroupNodeTestOK() {

        assertTrue(catalogManagerMongo.isLastGroupNode("groupNameOK"));

    }

    @Test
    public void isLastGroupNodeTestFail() {

        assertFalse(catalogManagerMongo.isLastGroupNode("groupNameErr"));

    }
}
