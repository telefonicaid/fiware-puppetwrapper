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

package com.telefonica.euro_iaas.sdc.puppetwrapper.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.ModuleDownloaderException;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.dto.NodeDto;
import com.telefonica.euro_iaas.sdc.puppetwrapper.dto.UrlDto;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ActionsService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ModuleDownloader;

/**
 * Presentation layer
 * 
 * @author alberts
 *
 */
@Controller
public class PuppetControllerv2 extends GenericController {

    private static final Logger LOG = LoggerFactory.getLogger(PuppetControllerv2.class);

    public static final String GITREPOSOURCE = "git";
    public static final String SVNREPOSOURCE = "svn";

    @Resource
    private ActionsService actionsService;

    @Resource
    private FileAccessService fileAccessService;

    @Resource
    private ModuleDownloader gitCloneService;

    @Resource
    private ModuleDownloader svnExporterService;

    /**
     * Stores a software to be installed on a node
     * 
     * @param nodeDto
     * @param nodeName
     * @param request
     * @return Node
     */
    @RequestMapping(value = "/v2/node/{nodeName}/install", method = RequestMethod.POST)
    @ResponseBody
    public Node install(@RequestBody NodeDto nodeDto, @PathVariable String nodeName, HttpServletRequest request) {

        if (nodeDto == null) {
            LOG.debug("Payload is missing");
            throw new IllegalArgumentException("Payload is missing");
        }

        if (nodeDto.getGroup() == null || "".equals(nodeDto.getGroup())) {
            LOG.debug("Group is not set");
            throw new IllegalArgumentException("Group is not set");
        }

        if (nodeName == null || "".equals(nodeName)) {
            LOG.debug("Node name is not set");
            throw new IllegalArgumentException("Node name is not set");
        }

        if (nodeDto.getSoftwareName() == null || "".equals(nodeDto.getSoftwareName())) {
            LOG.debug("Software Name is not set");
            throw new IllegalArgumentException("Software name is not set");
        }

        if (nodeDto.getVersion() == null || "".equals(nodeDto.getVersion())) {
            LOG.debug("version is not set");
            throw new IllegalArgumentException("Version is not set");
        }

        if (nodeDto.getAttributes() == null) {
            LOG.debug("attibutes are not set");
            throw new IllegalArgumentException("Attibutes are not set");
        }

        LOG.info("install group:" + nodeDto);

        Node node = actionsService.action(Action.INSTALL, nodeDto.getGroup(), nodeName, nodeDto.getSoftwareName(),
                nodeDto.getVersion(), nodeDto.getAttributes());

        LOG.debug("node " + node);

        return node;
    }

    /**
     * Generates the node manifest
     * 
     * @param nodeName
     * @return Node
     * @throws IOException
     */
    @RequestMapping(value = "/v2/node/{nodeName}/generate", method = RequestMethod.GET)
    @ResponseBody
    public Node generateManifest(@PathVariable("nodeName") String nodeName) throws IOException {

        if (nodeName == null || "".equals(nodeName)) {
            throw new IllegalArgumentException("Node name is not set");
        }
        LOG.info("generating files for node:" + nodeName);

        Node node = fileAccessService.generateManifestFile(nodeName);
        LOG.debug("nodes pp files OK");

        fileAccessService.generateSiteFile();
        LOG.debug("site.pp OK");

        return node;
    }

    /**
     * Deletes a software to be installed
     * 
     * @param nodeDto
     * @param nodeName
     * @param request
     * @return Node
     */
    @RequestMapping(value = "/v2/node/{nodeName}/uninstall", method = RequestMethod.POST)
    @ResponseBody
    public Node uninstall(@RequestBody NodeDto nodeDto, @PathVariable String nodeName, HttpServletRequest request) {

        if (nodeDto == null) {
            LOG.debug("Payload is missing");
            throw new IllegalArgumentException("Payload is missing");
        }

        if (nodeDto.getGroup() == null || "".equals(nodeDto.getGroup())) {
            LOG.debug("Group is not set");
            throw new IllegalArgumentException("Group is not set");
        }

        if (nodeName == null || "".equals(nodeName)) {
            LOG.debug("Node name is not set");
            throw new IllegalArgumentException("Node name is not set");
        }

        if (nodeDto.getSoftwareName() == null || "".equals(nodeDto.getSoftwareName())) {
            LOG.debug("Software Name is not set");
            throw new IllegalArgumentException("Software name is not set");
        }

        if (nodeDto.getVersion() == null || "".equals(nodeDto.getVersion())) {
            LOG.debug("version is not set");
            throw new IllegalArgumentException("Version is not set");
        }

        LOG.info("install group:" + nodeDto.getGroup() + " nodeName: " + nodeName + " soft: "
                + nodeDto.getSoftwareName() + " version: " + nodeDto.getVersion());

        Node node = actionsService.action(Action.UNINSTALL, nodeDto.getGroup(), nodeName, nodeDto.getSoftwareName(),
                nodeDto.getVersion(), null);

        LOG.debug("node " + node);

        return node;

    }

    /**
     * Delete a stored node
     * 
     * @param nodeName
     * @throws IOException
     */
    @RequestMapping(value = "/v2/node/{nodeName}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteNode(@PathVariable("nodeName") String nodeName) throws IOException {

        if (nodeName == null || "".equals(nodeName)) {
            LOG.debug("Node name is not set");
            throw new IllegalArgumentException("Node name is not set");
        }

        LOG.info("Deleting node: " + nodeName);
        actionsService.deleteNode(nodeName);
        LOG.info("Node: " + nodeName + " deleted.");
    }

    /**
     * Downloads a module from a SCM and stores it
     * 
     * @param softwareName
     * @param urlDto
     * @throws ModuleDownloaderException
     */
    @RequestMapping(value = "/v2/module/{softwareName}/download", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void downloadModule(@PathVariable("softwareName") String softwareName, @RequestBody UrlDto urlDto)
            throws ModuleDownloaderException {

        if (urlDto == null) {
            LOG.debug("Payload is missing");
            throw new IllegalArgumentException("Payload is missing");
        }

        if (softwareName == null || "".equals(softwareName)) {
            LOG.debug("Software name is not set");
            throw new IllegalArgumentException("Software name is not set");
        }

        if (urlDto.getUrl() == null || "".equals(urlDto.getUrl())) {
            LOG.debug("Url is not set");
            throw new IllegalArgumentException("Url is not set");
        }

        if (urlDto.getRepoSource() == null || "".equals(urlDto.getRepoSource())) {
            LOG.debug("repoSource is not set");
            throw new IllegalArgumentException("repoSource is not set");
        }

        if (GITREPOSOURCE.equals(urlDto.getRepoSource())) {
            gitCloneService.download(urlDto.getUrl(), softwareName);
        } else if (SVNREPOSOURCE.equals(urlDto.getRepoSource())) {
            svnExporterService.download(urlDto.getUrl(), softwareName);
        } else {
            throw new ModuleDownloaderException("RepoSource parameter is incorrect");
        }

    }

    /**
     * Deletes a module
     * 
     * @param moduleName
     * @throws IOException
     */
    @RequestMapping(value = "/v2/module/{moduleName}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteModule(@PathVariable("moduleName") String moduleName) throws IOException {

        if (moduleName == null || "".equals(moduleName)) {
            LOG.debug("Module name is not set");
            throw new IllegalArgumentException("Module name is not set");
        }

        LOG.info("Deleting module: " + moduleName);
        actionsService.deleteModule(moduleName);
        LOG.info("Module: " + moduleName + " deleted.");
    }

    public void setActionsService(ActionsService actionsService) {
        this.actionsService = actionsService;
    }

    public void setFileAccessService(FileAccessService fileAccessService) {
        this.fileAccessService = fileAccessService;
    }

    public void setGitCloneService(ModuleDownloader gitCloneService) {
        this.gitCloneService = gitCloneService;
    }

    public void setSvnExporterService(ModuleDownloader svnExporterService) {
        this.svnExporterService = svnExporterService;
    }

}
