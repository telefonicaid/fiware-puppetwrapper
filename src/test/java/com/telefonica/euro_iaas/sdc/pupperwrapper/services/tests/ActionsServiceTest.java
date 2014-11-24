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
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Attribute;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl.CatalogManagerMongoImpl;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl.FileAccessServiceImpl;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl.ProcessBuilderFactory;

public class ActionsServiceTest {

    private ActionServiceImpl4Test actionsService;

    private CatalogManager catalogManagerMongo;

    private ProcessBuilderFactory processBuilderFactory;

    private Node node1;
    private Node node1Modified;
    private List<Attribute> attributeList;
    private Attribute attribute1;

    private HttpClient client;
    private HttpResponse response;
    private HttpEntity entity;
    private StatusLine statusLine;

    @Before
    public void setUpMock() throws Exception {
        catalogManagerMongo = mock(CatalogManagerMongoImpl.class);

        FileAccessService fileAccessService = mock(FileAccessServiceImpl.class);

        processBuilderFactory = mock(ProcessBuilderFactory.class);

        response = mock(HttpResponse.class);
        entity = mock(HttpEntity.class);
        statusLine = mock(StatusLine.class);
        HttpClient httpClient = mock(HttpClient.class);

        when(httpClient.execute((HttpUriRequest) Mockito.anyObject())).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
        String source = "";
        InputStream in = IOUtils.toInputStream(source, "UTF-8");
        when(entity.getContent()).thenReturn(in);
        when(response.getStatusLine()).thenReturn(statusLine);

        actionsService = new ActionServiceImpl4Test();
        actionsService.setCatalogManager(catalogManagerMongo);
        actionsService.setFileAccessService(fileAccessService);
        actionsService.setProcessBuilderFactory(processBuilderFactory);

        actionsService.setHttpClient(httpClient);

        node1 = new Node();
        node1.setGroupName("testGroup");
        node1.setId("1");
        Software soft1 = new Software();
        soft1.setName("testSoft");
        soft1.setAction(Action.INSTALL);
        soft1.setVersion("1.0.0");
        node1.addSoftware(soft1);

        node1Modified = new Node();
        node1Modified.setGroupName("testGroup");
        node1Modified.setId("1");
        Software soft1Modified = new Software();
        soft1Modified.setName("testSoft");
        soft1Modified.setAction(Action.INSTALL);
        soft1Modified.setVersion("2.0.0");
        node1.addSoftware(soft1Modified);

        attribute1 = new Attribute("user", "pepito");
        attributeList = new ArrayList<Attribute>();
        attributeList.add(attribute1);

    }

    @Test
    public void install() {

        when(catalogManagerMongo.getNode("1")).thenThrow(new NoSuchElementException()).thenReturn(node1);

        actionsService.action(Action.INSTALL, "testGroup", "1", "testSoft", "1.0.0", attributeList);

        Node node = catalogManagerMongo.getNode("1");
        Software soft = node.getSoftware("testSoft");

        assertTrue(node != null);
        assertTrue(soft != null);
        assertTrue(node.getGroupName().equals("testGroup"));
        assertTrue(node.getId().equals("1"));
        assertTrue(soft.getName().equals("testSoft"));
        assertTrue(soft.getVersion().equals("1.0.0"));
        assertTrue(soft.getAction().equals(Action.INSTALL));

    }

    @Test
    public void uninstallTest() {

        when(catalogManagerMongo.getNode("1")).thenReturn(node1);

        Node node = actionsService.action(Action.UNINSTALL, "testGroup", "1", "testSoft", "1.0.0", attributeList);

        Software soft = node.getSoftware("testSoft");

        assertTrue(node != null);
        assertTrue(soft != null);
        assertTrue(node.getGroupName().equals("testGroup"));
        assertTrue(node.getId().equals("1"));
        assertTrue(soft.getName().equals("testSoft"));
        assertTrue(soft.getVersion().equals("1.0.0"));
        assertTrue(soft.getAction().equals(Action.UNINSTALL));

    }

    @Test
    public void uninstallModificationSoft() {

        when(catalogManagerMongo.getNode("1")).thenReturn(node1);

        actionsService.action(Action.UNINSTALL, "testGroup", "1", "testSoft", "1.0.0", attributeList);
        actionsService.action(Action.UNINSTALL, "testGroup", "1", "testSoft", "2.0.0", attributeList);

        Node node = catalogManagerMongo.getNode("1");
        Software soft = node.getSoftware("testSoft");

        verify(catalogManagerMongo, times(2)).addNode((Node) anyObject());

    }

    @Test(expected = NoSuchElementException.class)
    public void uninstallSoftNotExists() {

        when(catalogManagerMongo.getNode("1")).thenReturn(node1);

        actionsService.action(Action.UNINSTALL, "testGroup", "1", "testSoftNoExists", "1.0.0", attributeList);

        verify(catalogManagerMongo, times(1)).getNode(anyString());
    }

    @Test(expected = NoSuchElementException.class)
    public void uninstallNodeNotExists() {

        when(catalogManagerMongo.getNode("nodenoexists")).thenThrow(new NoSuchElementException());

        actionsService.action(Action.UNINSTALL, "groupnoexists", "nodenoexists", "testSoft", "1.0.0", attributeList);

        verify(catalogManagerMongo, times(1)).getNode(anyString());
    }

    @Test(expected = NoSuchElementException.class)
    public void uninstallSoftNotExists2() {

        when(catalogManagerMongo.getNode("nodenoexists")).thenThrow(new NoSuchElementException());

        actionsService.action(Action.UNINSTALL, "testGroup", "nodenoexists", "softnoexists", "1.0.0", attributeList);
    }

    @Test
    public void deleteNodeTestOK() throws IOException {

        Process shell = mock(Process.class);
        Process shell2 = mock(Process.class);
        Process shellNodeName = mock(Process.class);

        String[] cmd = { anyString() };
        // call to puppet cert list --all
        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shellNodeName).thenReturn(shell)
                .thenReturn(shell2);

        String strNodeName = "\"1.novalocal\"";
        when(shellNodeName.getInputStream()).thenReturn(new ByteArrayInputStream(strNodeName.getBytes("UTF-8")));
        when(shellNodeName.getErrorStream()).thenReturn(new ByteArrayInputStream(" ".getBytes("UTF-8")));

        String str = "Node 1.novalocal is registered";
        String strdelete = "Node 1 unregistered";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8"))).thenReturn(
                new ByteArrayInputStream(strdelete.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        String str2 = "1.novalocal";
        when(shell2.getInputStream()).thenReturn(new ByteArrayInputStream(str2.getBytes("UTF-8")));

        String strEr2 = " ";
        when(shell2.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr2.getBytes("UTF-8")));

        when(catalogManagerMongo.getNode("1")).thenReturn(node1).thenThrow(new NoSuchElementException())
                .thenReturn(node1);

        when(statusLine.getStatusCode()).thenReturn(200);

        actionsService.deleteNode("1");

        verify(shell, times(1)).getInputStream();
        verify(shell2, times(1)).getInputStream();
        verify(shellNodeName, times(1)).getInputStream();
        verify(processBuilderFactory, times(3)).createProcessBuilder((String[]) anyObject());

    }

    @Test(expected = IOException.class)
    public void deleteNodeTestException() throws IOException {

        Process shell = mock(Process.class);
        Process shellNodeName = mock(Process.class);

        String[] cmd = { anyString() };
        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shellNodeName).thenReturn(shellNodeName)
                .thenReturn(shell);

        String strNodeName = "1.novalocal";
        when(shellNodeName.getInputStream()).thenReturn(new ByteArrayInputStream(strNodeName.getBytes("UTF-8")))
                .thenReturn(new ByteArrayInputStream(strNodeName.getBytes("UTF-8")));
        when(shellNodeName.getErrorStream()).thenReturn(new ByteArrayInputStream(" ".getBytes("UTF-8")));

        String str = "";
        String strdelete = "";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8"))).thenReturn(
                new ByteArrayInputStream(strdelete.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        when(catalogManagerMongo.getNode("1")).thenReturn(node1).thenThrow(new NoSuchElementException())
                .thenReturn(node1);

        // delete node 1

        actionsService.deleteNode("1");

        verify(shell, times(1)).getInputStream();
        verify(processBuilderFactory, times(3)).createProcessBuilder((String[]) anyObject());

    }

    @Test(expected = IOException.class)
    public void deleteNodeTestPuupetDBConnError() throws IOException {

        Process shell = mock(Process.class);
        Process shell2 = mock(Process.class);
        Process shellNodeName = mock(Process.class);

        String[] cmd = { anyString() };
        // call to puppet cert list --all
        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shellNodeName).thenReturn(shell)
                .thenReturn(shell2);

        String strNodeName = "nodename";
        when(shellNodeName.getInputStream()).thenReturn(new ByteArrayInputStream(strNodeName.getBytes("UTF-8")));
        when(shellNodeName.getErrorStream()).thenReturn(new ByteArrayInputStream(" ".getBytes("UTF-8")));

        String str = "Node 1 is registered";
        String strdelete = "Node 1 unregistered";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8"))).thenReturn(
                new ByteArrayInputStream(strdelete.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        String str2 = "Node1.novalocal";
        when(shell2.getInputStream()).thenReturn(new ByteArrayInputStream(str2.getBytes("UTF-8")));

        String strEr2 = " ";
        when(shell2.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr2.getBytes("UTF-8")));

        when(catalogManagerMongo.getNode("1")).thenReturn(node1).thenThrow(new NoSuchElementException())
                .thenReturn(node1);

        actionsService.deleteNode("1");

        verify(shell, times(1)).getInputStream();
        verify(shell2, times(2)).getInputStream();
        verify(processBuilderFactory, times(3)).createProcessBuilder((String[]) anyObject());

    }

    @Test(expected = NoSuchElementException.class)
    public void deleteNodeNotFoundTest() throws IOException {

        Process shell = mock(Process.class);
        Process shell2 = mock(Process.class);
        Process shellNodeName = mock(Process.class);

        String[] cmd = { anyString() };
        // call to puppet cert list --all
        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shellNodeName).thenReturn(shell)
                .thenReturn(shell2);

        String strNodeName = "\"1.novalocal\"";
        when(shellNodeName.getInputStream()).thenReturn(new ByteArrayInputStream(strNodeName.getBytes("UTF-8")));
        when(shellNodeName.getErrorStream()).thenReturn(new ByteArrayInputStream(" ".getBytes("UTF-8")));

        String str = "Node 1.novalocal is registered";
        String strdelete = "Node 1 unregistered";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8"))).thenReturn(
                new ByteArrayInputStream(strdelete.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        String str2 = "1.novalocal";
        when(shell2.getInputStream()).thenReturn(new ByteArrayInputStream(str2.getBytes("UTF-8")));

        String strEr2 = " ";
        when(shell2.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr2.getBytes("UTF-8")));

        when(catalogManagerMongo.getNode("1")).thenThrow(new NoSuchElementException()).thenReturn(node1);

        when(statusLine.getStatusCode()).thenReturn(200);

        actionsService.deleteNode("1");

        verify(shell, times(1)).getInputStream();
        verify(shell2, times(1)).getInputStream();
        verify(shellNodeName, times(1)).getInputStream();
        verify(processBuilderFactory, times(3)).createProcessBuilder((String[]) anyObject());

    }

    @Test
    public void isNodeRegisteredNO() throws IOException {

        Process shell = mock(Process.class);

        String[] cmd = { anyString() };
        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shell);

        String str = "Node 3 is registered";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        Assert.assertFalse(actionsService.isNodeRegistered("1"));

    }

    @Test
    public void isNodeRegisteredYES() throws IOException {

        Process shell = mock(Process.class);

        String[] cmd = { anyString() };

        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shell);

        String str = "Node 1 is registered";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        Assert.assertTrue(actionsService.isNodeRegistered("1"));

    }

    @Test(expected = IOException.class)
    public void isNodeRegisteredException() throws IOException {

        Process shell = mock(Process.class);

        String[] cmd = { anyString() };
        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shell);

        String str = "";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        Assert.assertTrue(actionsService.isNodeRegistered("1"));

    }

    @Test()
    public void getRealNodeNameTest() throws IOException {

        Process shell = mock(Process.class);

        String[] cmd = { anyString() };

        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shell).thenReturn(shell);

        String str = "\"testnodename.openstacklocal\"";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8"))).thenReturn(
                new ByteArrayInputStream(str.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8"))).thenReturn(
                new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        Assert.assertTrue("testnodename.openstacklocal".equals(actionsService.getRealNodeName("testnodename")));

    }

    @Test(expected = IOException.class)
    public void getRealNodeNameTestEmpptyString() throws IOException {

        Process shell = mock(Process.class);

        String[] cmd = { anyString() };

        when(processBuilderFactory.createProcessBuilder(cmd)).thenReturn(shell).thenReturn(shell);

        String str = "\"testnodename.openstacklocal\"";
        when(shell.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes("UTF-8")));

        String strEr = " ";
        when(shell.getErrorStream()).thenReturn(new ByteArrayInputStream(strEr.getBytes("UTF-8")));

        Assert.assertTrue("testnodename.openstacklocal".equals(actionsService.getRealNodeName("testnodename")));

    }

}
