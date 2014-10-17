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

package com.telefonica.euro_iaas.sdc.pupperwrapper.controllers.tests;


import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.controllers.PuppetController;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Attribute;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ActionsService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ModuleDownloader;

public class PuppetControllerTest {

    private ActionsService actionsService;

    private FileAccessService fileAccessService;

    private CatalogManager catalogManager;

    private ModuleDownloader gitCloneService;

    private ModuleDownloader svnExporterService;

    private PuppetController puppetController;

    private HttpServletRequest request;

    private Node node;
    private Software soft;

    @Before
    public void setup() {

        request = mock(HttpServletRequest.class);

        actionsService = mock(ActionsService.class);
        fileAccessService = mock(FileAccessService.class);
        catalogManager = mock(CatalogManager.class);
        gitCloneService = mock(ModuleDownloader.class);
        svnExporterService = mock(ModuleDownloader.class);

        puppetController = new PuppetController();

        puppetController.setActionsService(actionsService);
        puppetController.setFileAccessService(fileAccessService);
        puppetController.setCatalogManager(catalogManager);
        puppetController.setGitCloneService(gitCloneService);
        puppetController.setSvnExporterService(svnExporterService);

        node = new Node();
        node.setId("test");
        node.setGroupName("group");
        node.setManifestGenerated(true);

        Software soft = new Software();
        soft.setName("test");
        soft.setVersion("1.0.0");
        soft.setAction(Action.INSTALL);
        node.addSoftware(soft);

        node.addSoftware(soft);

    }

    @Test
    public void installTest() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.install("group", "nodeName", "softwareName", "1", request);

        verify(actionsService, times(1))
                .action((Action) anyObject(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        (List<Attribute>) anyObject());

    }

    @Test(expected = IllegalArgumentException.class)
    public void installTestMissingParam1() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.install(null, "nodeName", "softwareName", "1", request);


    }

    @Test(expected = IllegalArgumentException.class)
    public void installTestMissingParam2() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.install("group", null, "softwareName", "1", request);


    }

    @Test(expected = IllegalArgumentException.class)
    public void installTestMissingParam3() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.install("group", "nodeName", null, "1", request);


    }

    @Test(expected = IllegalArgumentException.class)
    public void installTestMissingParam4() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.install("group", "nodeName", "softwareName", null, request);
    }

    @Test
    public void unInstallTest() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.uninstall("group", "nodeName", "softwareName", "1", request);

        verify(actionsService, times(1)).action(
                    (Action) anyObject(),
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    (List<Attribute>) anyObject());

    }

    @Test(expected = IllegalArgumentException.class)
    public void unInstallTestMissingParam1() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.uninstall(null, "nodeName", "softwareName", "1", request);

    }

    @Test(expected = IllegalArgumentException.class)
    public void unInstallTestMissingParam2() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.uninstall("group", null, "softwareName", "1", request);

    }

    @Test(expected = IllegalArgumentException.class)
    public void unInstallTestMissingParam3() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.uninstall("group", "nodeName", null, "1", request);

    }

    @Test(expected = IllegalArgumentException.class)
    public void unInstallTestMissingParam4() {
        when(actionsService.action(
                eq(Action.INSTALL), anyString(), anyString(), anyString(), anyString(), (List<Attribute>) anyObject()))
                .thenReturn(node);

        puppetController.uninstall("group", "nodeName", "softwareName", null, request);

    }

    @Test
    public void generateTest() throws IOException {

        when(fileAccessService.generateManifestFile(anyString())).thenReturn(node);

        puppetController.generateManifest("node");

        verify(fileAccessService, times(1)).generateManifestFile(anyString());
        verify(fileAccessService, times(1)).generateSiteFile();

    }

    @Test(expected = IllegalArgumentException.class)
    public void generateTestMissingParam() throws IOException {

        when(fileAccessService.generateManifestFile(anyString())).thenReturn(node);

        puppetController.generateManifest("");

    }

    @Test(expected = IOException.class)
    public void generateTestIOException() throws IOException {

        when(fileAccessService.generateManifestFile(anyString())).thenThrow(new IOException());

        puppetController.generateManifest("node");


    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNodeTestMissingParam() throws IOException {

        puppetController.deleteNode(null);

    }

    @Test(expected = NoSuchElementException.class)
    public void deleteNodeTestNodeNoExists() throws IOException {

        doThrow(new NoSuchElementException()).when(actionsService).deleteNode("node");

        puppetController.deleteNode("node");

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteModuleTestMissingParam() throws IOException {

        puppetController.deleteModule(null);

    }

    @Test(expected = NoSuchElementException.class)
    public void deleteModuleTestModuleNoExists() throws IOException {

        doThrow(new NoSuchElementException()).when(actionsService).deleteModule("soft");

        puppetController.deleteModule("soft");

    }


}
