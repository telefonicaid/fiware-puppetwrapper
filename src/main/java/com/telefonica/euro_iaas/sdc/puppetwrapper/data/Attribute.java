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

package com.telefonica.euro_iaas.sdc.puppetwrapper.data;


public class Attribute {

    private Long id;
    private Long v;
    private String key;
    private String value;
    private String description;

 
    public Attribute() {
    }

    public Attribute(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Attribute(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String toString() {
       StringBuilder sb = new StringBuilder("[[Attribute]");
       sb.append("[id = ").append(this.id).append("]");
       sb.append("[v = ").append(this.v).append("]");
       sb.append("[key = ").append(this.key).append("]");
       sb.append("[value = ").append(this.value).append("]");
       sb.append("[description = ").append(this.description).append("]");
       sb.append("]");
       return sb.toString();
    }
    
    

}
