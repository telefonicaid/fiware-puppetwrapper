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

package com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl;

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Attribute;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ActionsService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService;

@Service("actionsService")
public class ActionsServiceImpl implements ActionsService {

    private Logger log = LoggerFactory.getLogger(ActionsServiceImpl.class);

    private String puppetDBUrl;

    @SuppressWarnings("restriction")
    @Resource
    protected CatalogManager catalogManager;

    @Resource
    protected FileAccessService fileAccessService;

    @Resource
    protected HttpClient httpClient;

    @Resource
    protected ProcessBuilderFactory processBuilderFactory;

    public Node action(Action action, String group, String nodeName, String softName, String version,
            List<Attribute> attributes) {

        log.info("action: " + action + "group:" + group + " nodeName: " + nodeName + " soft: " + softName
                + " version: " + version);

        Node node = null;
        try {
            node = catalogManager.getNode(nodeName);
            node.setGroupName(group);
        } catch (NoSuchElementException e) {
            if (Action.UNINSTALL.equals(action)) {
                throw e;
            }
            node = new Node();
            node.setId(nodeName);
            node.setGroupName(group);
        }

        Software soft = null;
        try {
            soft = node.getSoftware(softName);
            soft.setVersion(version);
            soft.setAction(action);
            soft.setAttributes(attributes);
        } catch (NoSuchElementException e) {
            if (Action.UNINSTALL.equals(action)) {
                throw e;
            }
            soft = new Software();
            soft.setName(softName);
            soft.setVersion(version);
            soft.setAction(action);
            soft.setAttributes(attributes);
            node.addSoftware(soft);
        }

        catalogManager.addNode(node);

        log.debug("node: " + node);

        return node;

    }

    public void deleteNode(String nodeName) throws IOException {
        fileAccessService.deleteNodeFiles(nodeName);
        catalogManager.removeNode(nodeName);

        // generate the file again to make sure there are no empty directories
        fileAccessService.generateSiteFile();

        String realNodeName = getRealNodeName(nodeName);

        uregisterNode(realNodeName);
        deactivateNodeFromPuppetDB(realNodeName);

    }

    private void uregisterNode(String nodeName) throws IOException {

        log.debug("Unregistering node: " + nodeName);

        if (isNodeRegistered(nodeName)) {
            log.debug("Node " + nodeName + " is registered -> unregistering");

            String[] cmd = { "/bin/sh", "-c", "sudo puppet cert clean " + nodeName };

            Process shell = processBuilderFactory.createProcessBuilder(cmd);

            StringBuilder success = new StringBuilder();
            StringBuilder error = new StringBuilder();

            executeSystemCommand(shell, success, error);

            if ("".equals(success) && !"".equals(error)) {
                throw new IOException("Puppet cert clean has failed");
            }
        }

    }

    private void deactivateNodeFromPuppetDB(String nodeName) throws IOException {
        log.debug("Deactivating node: " + nodeName);

        String path = "/v3/commands";
        String url = puppetDBUrl + path;
        log.info("conneting puppetdb: " + url);

        try {
            HttpPost post = new HttpPost(url);
            post.addHeader("Accept", "application/json");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            String payload = "payload={\"command\":\"deactivate node\",\"version\": 1,\"payload\":\"" + nodeName
                    + "\"}";
            log.info("payload: " + payload);
            post.setEntity(new StringEntity(payload));

            HttpResponse response = httpClient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

            if (statusCode != 200) {
                String msg = format("[puppetdb deactivate node] response code was: {0}", statusCode);
                log.warn(msg);
                throw new IOException(format(msg));
            } else {
                log.info("Node " + nodeName + " deactivated from puppetDB");
            }

        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new IOException("It is not possible to connect with puppetDB server Url " + e.getMessage());
        }
    }

    public String getRealNodeName(String nodeName) throws IOException {

        log.debug("getRealNodeName for node: " + nodeName);

        String name = "";
        if (isNodeRegistered(nodeName)) {

            String[] cmd = { "/bin/sh", "-c", "sudo puppet cert list --all | grep " + nodeName + " | gawk '{print $2}'" };

            Process shell = processBuilderFactory.createProcessBuilder(cmd);

            StringBuilder success = new StringBuilder();
            StringBuilder error = new StringBuilder();

            executeSystemCommand(shell, success, error);

            if ("".equals(success) && !"".equals(error)) {
                throw new IOException("Puppet cert list has failed");
            }
            log.debug("success, real name is: " + success);

            if (success.length() > 1) {
                name = success.substring(1, success.length() - 1);
            }else{
                throw new IOException("Error obtaining realNodename. Command returned an empty string");
            }

            log.debug("name: " + name);
        }

        return name;

    }

    public boolean isNodeRegistered(String nodeName) throws IOException {

        log.debug("isNodeRegistered node: " + nodeName);

        String[] cmd = { "/bin/sh", "-c", "sudo puppet cert list --all" };
        Process shell = processBuilderFactory.createProcessBuilder(cmd);

        StringBuilder successResponse = new StringBuilder();
        StringBuilder errorResponse = new StringBuilder();

        executeSystemCommand(shell, successResponse, errorResponse);

        String str = (successResponse.length() == 0 ? "" : successResponse.toString());

        if (!"".equals(str)) {
            if (!successResponse.toString().contains(nodeName)) {
                // logger.debug("registered nodes: ");
                // logger.debug(str);
                return false;
            }

        } else {
            String msg = "Puppet cert list command has failed";
            log.debug(msg);
            throw new IOException(msg);
        }
        return true;

    }

    public void deleteGroup(String groupName) throws IOException {
        fileAccessService.deleteGoupFolder(groupName);
        catalogManager.removeNodesByGroupName(groupName);

    }

    public void executeSystemCommand(Process shell, StringBuilder successResponse, StringBuilder errorResponse)
            throws IOException {

        InputStream is = shell.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
            successResponse.append(line);
        }

        InputStream isEr = shell.getErrorStream();
        InputStreamReader isrEr = new InputStreamReader(isEr);
        BufferedReader brEr = new BufferedReader(isrEr);
        String lineEr;
        while ((lineEr = brEr.readLine()) != null) {
            System.out.println(lineEr);
            errorResponse.append(lineEr);
        }

    }

    // @Override
    public void deleteModule(String moduleName) throws IOException {
        fileAccessService.deleteModuleFiles(moduleName);

    }

    @Value(value = "${puppetDBUrl}")
    public void setPuppetDBUrl(String puppetDBUrl) {
        this.puppetDBUrl = puppetDBUrl;
    }

}
