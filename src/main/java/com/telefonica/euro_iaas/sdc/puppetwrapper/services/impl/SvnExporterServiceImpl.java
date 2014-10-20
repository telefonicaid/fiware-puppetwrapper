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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.ModuleDownloaderException;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ModuleDownloader;

/**
 * Class SvnExporterServiceImpl.
 * 
 * @author Albert Sinfreu Alay
 */
@Service("svnExporterService")
public class SvnExporterServiceImpl implements ModuleDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(SvnExporterServiceImpl.class);

    private String modulesCodeDownloadPath;

    private String username = "";
    private String password = "";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.telefonica.euro_iaas.sdc.puppetwrapper.services.ModuleDownloader#
     * download(java.lang.String, java.lang.String)
     */
    public void download(String url, String moduleName) throws ModuleDownloaderException {

        SVNRepository repository = null;

        try {
            if (url.contains("http://") || url.contains("https://")) {
                DAVRepositoryFactory.setup();
            } else if (url.contains("svn://")) {
                SVNRepositoryFactoryImpl.setup();
            }
            // initiate the reporitory from the url
            repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
            // create authentication data
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            repository.setAuthenticationManager(authManager);
            // output some data to verify connection
            LOG.debug("Repository Root: " + repository.getRepositoryRoot(true));
            LOG.debug("Repository UUID: " + repository.getRepositoryUUID(true));
            // need to identify latest revision
            long latestRevision = repository.getLatestRevision();
            LOG.debug("Repository Latest Revision: " + latestRevision);

            // create client manager and set authentication
            SVNClientManager ourClientManager = SVNClientManager.newInstance();
            ourClientManager.setAuthenticationManager(authManager);
            // use SVNUpdateClient to do the export
            SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
            updateClient.setIgnoreExternals(false);
            updateClient.doExport(repository.getLocation(), new File(modulesCodeDownloadPath + moduleName),
                    SVNRevision.create(latestRevision), SVNRevision.create(latestRevision), null, true,
                    SVNDepth.INFINITY);

        } catch (SVNException e) {
            throw new ModuleDownloaderException(e);
        } catch (Exception ex) {
            throw new ModuleDownloaderException(ex);
        }
        LOG.debug("Done");
    }

    @Value(value = "${modulesCodeDownloadPath}")
    public void setModulesCodeDownloadPath(String modulesCodeDownloadPath) {
        this.modulesCodeDownloadPath = modulesCodeDownloadPath;
    }
}
