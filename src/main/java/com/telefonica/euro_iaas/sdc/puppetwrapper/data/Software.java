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

import com.telefonica.euro_iaas.sdc.puppetwrapper.common.Action;

/**
 * Class Software.
 *
 * @author Albert Sinfrey Alay
 */
public class Software {

    private String eol = System.getProperty("line.separator");

    private String name;
    private String version;
    private Action action;

    /**
     * Constructor.
     */
    public Software() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * Generate file string.
     * @return
     */
    public String generateFileStr() {
        StringBuffer sb = new StringBuffer();
        sb.append("  class{'" + this.name + "::" + action.getActionString(action.getCode()) + "':");
        sb.append(eol);
        sb.append("   version => '" + version + "',");
        sb.append(eol);
        sb.append("  }");
        sb.append(eol);

        return sb.toString();

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[Software]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[version = ").append(this.version).append("]");
        sb.append("[action = ").append(this.action).append("]");
        sb.append("]");
        return sb.toString();
    }


}
