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

package com.telefonica.euro_iaas.sdc.puppetwrapper.services;

import java.io.IOException;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;

/**
 * Deals with the filesystem file generation
 * 
 * @author alberts
 *
 */
public interface FileAccessService {

    /**
     * Create node manifest file
     * 
     * @param nodeName
     * @return Node
     * @throws IOException
     */
    Node generateManifestFile(String nodeName) throws IOException;

    /**
     * Create site.pp file
     * 
     * @throws IOException
     */
    void generateSiteFile() throws IOException;

    /**
     * Delete file
     * 
     * @param nodeName
     * @throws IOException
     */
    void deleteNodeFiles(String nodeName) throws IOException;

    /**
     * Delete group folder
     * 
     * @param groupName
     * @throws IOException
     */
    void deleteGoupFolder(String groupName) throws IOException;

    /**
     * Delete module
     * 
     * @param moduleName
     * @throws IOException
     */
    void deleteModuleFiles(String moduleName) throws IOException;

}
