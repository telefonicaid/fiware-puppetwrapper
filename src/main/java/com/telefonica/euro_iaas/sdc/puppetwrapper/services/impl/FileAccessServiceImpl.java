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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService;

@Service("fileAccessService")
public class FileAccessServiceImpl implements FileAccessService {

    private static final Logger log = LoggerFactory.getLogger(FileAccessServiceImpl.class);

    @Resource
    protected CatalogManager catalogManager;

    private String defaultManifestsPath;

    private String modulesCodeDownloadPath;

    public Node generateManifestFile(String nodeName) throws IOException {

        log.info("creating Manifest file for node: " + nodeName);

        Node node = catalogManager.getNode(nodeName);

        String fileContent = catalogManager.generateManifestStr(nodeName);
        String path = defaultManifestsPath + node.getGroupName();

        try {

            File f = new File(path);
            f.mkdirs();
            f.createNewFile();
        } catch (IOException ex) {
            log.debug("Error creating manifest paths and pp file", ex);
            throw new IOException("Error creating manifest paths and pp file");
        }

        try {
            FileWriter fw = new FileWriter(path + "/" + node.getId() + ".pp", false);
            fw.write(fileContent);
            fw.close();
        } catch (IOException ex) {
            log.debug("Error creating manifest paths and pp file", ex);
            throw new IOException("Error creating manifest paths and pp file");
        }

        log.debug("Manifest file created");

        node.setManifestGenerated(true);
        return node;

    }

    public void generateSiteFile() throws IOException {

        log.info("Generate site.pp");

        String fileContent = catalogManager.generateSiteStr();

        log.debug("site content: " + fileContent);
        log.debug("defaultManifestsPath: " + defaultManifestsPath);

        try {
            PrintWriter writer = new PrintWriter(defaultManifestsPath + "site.pp", "UTF-8");
            writer.println(fileContent);
            writer.close();
        } catch (IOException ex) {
            log.debug("Error creating site.pp file", ex);
            throw new IOException("Error creating site.pp file");
        }

        log.debug("Site.pp file created");
    }

    @Value(value = "${defaultManifestsPath}")
    public void setDefaultManifestsPath(String defaultManifestsPath) {
        this.defaultManifestsPath = defaultManifestsPath;
    }

    @Value(value = "${modulesCodeDownloadPath}")
    public void setDefaultModulesPath(String modulesCodeDownloadPath) {
        this.modulesCodeDownloadPath = modulesCodeDownloadPath;
    }

    public void deleteNodeFiles(String nodeName) throws IOException {

        try {
            
            Node node = catalogManager.getNode(nodeName);

            String path = defaultManifestsPath + node.getGroupName();

            File file = new File(path + "/" + node.getId() + ".pp");

            if (!file.delete()) {
                log.info(format("File {0} could not be deleted. Did it exist?", path + "/" + node.getId() + ".pp"));
            }else{
                log.info(format("File {0} deleted.", path + "/" + node.getId() + ".pp"));
            }
            
            if(catalogManager.isLastGroupNode(node.getGroupName())){
                deleteGoupFolder(node.getGroupName());
            }
            
        } catch (NoSuchElementException e) {
            log.info(format("Node {0} was not registered in puppet master",nodeName));
        }

    }

    public void deleteGoupFolder(String groupName) throws IOException {

        File path = new File(defaultManifestsPath + groupName);

        FileUtils.deleteDirectory(path);
        
        log.info(format("Folder {0} deleted.", path + "/" + groupName));
    }

//    @Override
    public void deleteModuleFiles(String moduleName) throws IOException {

        File file = new File(modulesCodeDownloadPath + moduleName);

        FileUtils.deleteDirectory(file);

        log.info(format("File {0} could not be deleted. Did it exist?", modulesCodeDownloadPath + "/" + moduleName
                + ".pp"));

    }

}
