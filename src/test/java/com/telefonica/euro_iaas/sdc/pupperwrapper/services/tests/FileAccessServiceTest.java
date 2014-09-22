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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl.CatalogManagerMongoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml" })
public class FileAccessServiceTest {

    private FileAccessServiceImpl4Test fileAccessService;

    private CatalogManager catalogManager;

    @Value("${defaultManifestsPath}")
    private String defaultManifestsPath;

    @Before
    public void setUp() {
        catalogManager = mock(CatalogManagerMongoImpl.class);

        fileAccessService = new FileAccessServiceImpl4Test();
        fileAccessService.setCatalogManager(catalogManager);
        fileAccessService.setDefaultManifestsPath(defaultManifestsPath);

        Node node = new Node();
        node.setId("test");
        node.setGroupName("group");

        Node node2 = new Node();
        node2.setId("test2");
        node2.setGroupName("group");

        Software soft = new Software();
        soft.setName("testSoft");
        soft.setVersion("1.0.0");
        soft.setAction(Action.INSTALL);

        node.addSoftware(soft);

        Software soft2 = new Software();
        soft2.setName("testSoft2");
        soft.setVersion("2.0.0");
        soft2.setAction(Action.INSTALL);

        node2.addSoftware(soft2);

        Node nodeMock = mock(Node.class);

        when(catalogManager.getNode("test")).thenReturn(node);
        when(catalogManager.getNode("test2")).thenReturn(node2);

        when(catalogManager.generateSiteStr()).thenReturn("site.pp content");
        when(catalogManager.generateManifestStr("test")).thenReturn("manifest test1 content");
        when(catalogManager.generateManifestStr("test2")).thenReturn("manifest test2 content");

        when(catalogManager.isLastGroupNode("group")).thenReturn(false).thenReturn(true);
    }

    @Test
    public void generateManifestFileTest() throws IOException {

        fileAccessService.generateManifestFile("test");
        fileAccessService.generateManifestFile("test2");

        File f = new File(defaultManifestsPath + "group/test.pp");
        assertTrue(f.exists());

        File f2 = new File(defaultManifestsPath + "group/test2.pp");
        assertTrue(f2.exists());
    }

    @Test(expected = NoSuchElementException.class)
    public void generateManifestNodeNotExists() throws IOException {
        when(catalogManager.getNode("nodenotexists")).thenThrow(new NoSuchElementException());

        fileAccessService.generateManifestFile("nodenotexists");
    }

    @Test
    public void generateSiteFileTest() throws IOException {

        fileAccessService.generateSiteFile();

        File f = new File(defaultManifestsPath + "site.pp");
        assertTrue(f.exists());


    }

    @Test
    public void deleteNodeTest() throws IOException {

        fileAccessService.generateSiteFile();
        fileAccessService.generateManifestFile("test");
        fileAccessService.generateManifestFile("test2");

        // deleting

        fileAccessService.deleteNodeFiles("test");

        File f = new File(defaultManifestsPath + "site.pp");
        assertTrue(f.exists());

        f = new File(defaultManifestsPath + "group/test.pp");
        assertFalse(f.exists());

        f = new File(defaultManifestsPath + "group/test2.pp");
        assertTrue(f.exists());

    }

    @Test
    public void deleteGoupFolder() throws IOException {

        fileAccessService.generateSiteFile();
        fileAccessService.generateManifestFile("test");
        fileAccessService.generateManifestFile("test2");

        // deleting

        fileAccessService.deleteGoupFolder("group");

        File f = new File(defaultManifestsPath + "site.pp");
        assertTrue(f.exists());

        f = new File(defaultManifestsPath + "group");
        assertFalse(f.exists());

    }

}
