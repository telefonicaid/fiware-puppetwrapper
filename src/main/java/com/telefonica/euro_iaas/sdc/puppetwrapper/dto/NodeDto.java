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

package com.telefonica.euro_iaas.sdc.puppetwrapper.dto;

import java.util.List;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Attribute;


public class NodeDto {
    
    private String group;
    private String softwareName;
    private String version;
    private List<Attribute> attibutes;
    
    public NodeDto(){
        
    }

    public NodeDto(String group, String softwareName, String version, List<Attribute> attibutes) {
        super();
        this.group = group;
        this.softwareName = softwareName;
        this.version = version;
        this.attibutes = attibutes;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Attribute> getAttibutes() {
        return attibutes;
    }

    public void setAttibutes(List<Attribute> attibutes) {
        this.attibutes = attibutes;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    public String toString() {
       StringBuilder sb = new StringBuilder("[[NodeDto]");
       sb.append("[group = ").append(this.group).append("]");
       sb.append("[softwareName = ").append(this.softwareName).append("]");
       sb.append("[version = ").append(this.version).append("]");
       sb.append("[attibutes = ").append(this.attibutes).append("]");
       sb.append("]");
       return sb.toString();
    }
    
    

}
