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

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;

/**
 * Persistence interaction 
 * @author alberts
 *
 */
public interface CatalogManager {


    /**
     * Store a node
     * @param node
     */
    void addNode(Node node);

    /**
     * Retreive a node
     * @param nodeName
     * @return Node
     */
    Node getNode(String nodeName);

    /**
     * Delete a node
     * @param nodeName
     */
    void removeNode(String nodeName);
    
    /**
     * Node length
     * @return
     */
    int getNodeLength();

    /**
     * Retrieves a node and generates manifest string
     * @param nodeName
     * @return string with manifest content
     */
    String generateManifestStr(String nodeName);

    /**
     * Generates site.pp content string
     * @return
     */
    String generateSiteStr();

    /**
     * Delete nodes
     * @param groupName
     */
    void removeNodesByGroupName(String groupName);
    
    /**
     * whether a given node is the last one of a user to be deleted
     * @param groupName
     * @return
     */
    boolean isLastGroupNode(String groupName);
}
