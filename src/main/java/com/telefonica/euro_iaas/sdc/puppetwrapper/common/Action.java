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

package com.telefonica.euro_iaas.sdc.puppetwrapper.common;

import static java.text.MessageFormat.format;

import java.util.NoSuchElementException;

/**
 * Class to manage the different actions that we have to take.
 * @author albert.sinfreualay
 */
public enum Action {
    INSTALL(1, "install"),
    UNINSTALL(2, "uninstall");

    private final int code;
    private final String description;

    /**
     * Constructor.
     * @param code
     * @param description
     */
    Action(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get an action identify by its id.
     * @param id
     * @return
     */
    public String getActionString(int id) {

        for (Action action : Action.values()) {
            if (action.getCode() == code) {
                return action.getDescription();
            }
        }
        throw new NoSuchElementException(format("Value not defined: [{0}]", code));
    }


}
