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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.ModuleDownloaderException;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ModuleDownloader;


@Service("gitCloneService")
public class GitCloneServiceImpl implements ModuleDownloader {

    private static final Logger log = LoggerFactory.getLogger(GitCloneServiceImpl.class);
    
    private String modulesCodeDownloadPath;
    
    public void download(String url,String moduleName) throws ModuleDownloaderException {
        // prepare a new folder for the cloned repository
        File localPath =  new File(modulesCodeDownloadPath+moduleName);
        localPath.delete();
        
        try {
            FileUtils.deleteDirectory(localPath);
        } catch (IOException e) {
            throw new ModuleDownloaderException(e);
        }

        // then clone
        log.debug("Cloning from " + url + " to " + localPath);
        try {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(localPath)
                    .call();
        } catch (InvalidRemoteException e) {
            throw new ModuleDownloaderException(e);
        } catch (TransportException e) {
            throw new ModuleDownloaderException(e);
        } catch (GitAPIException e) {
            throw new ModuleDownloaderException(e);
        }

        // now open the created repository
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository;
        try {
            repository = builder.setGitDir(localPath)
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
        } catch (IOException e) {
            throw new ModuleDownloaderException(e);
        }

        log.debug("Having repository: " + repository.getDirectory());

        repository.close();
    }
    
    @Value(value = "${modulesCodeDownloadPath}")
    public void setModulesCodeDownloadPath(String modulesCodeDownloadPath) {
        this.modulesCodeDownloadPath = modulesCodeDownloadPath;
    }
}

