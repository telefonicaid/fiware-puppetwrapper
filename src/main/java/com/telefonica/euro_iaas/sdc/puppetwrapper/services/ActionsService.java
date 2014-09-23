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
import java.util.List;

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Attribute;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;

public interface ActionsService {

    Node action(Action action, String group, String nodeName, String softName, String version,
            List<Attribute> attibutes);

    void deleteNode(String nodeName) throws IOException;

    void deleteGroup(String groupName) throws IOException;

    public void deleteModule(String moduleName) throws IOException;

    public boolean isNodeRegistered(String nodeName) throws IOException;

    public String getRealNodeName(String nodeName) throws IOException;

    public void executeSystemCommand(Process shell, StringBuilder successResponse, StringBuilder errorResponse)
            throws IOException;
}
