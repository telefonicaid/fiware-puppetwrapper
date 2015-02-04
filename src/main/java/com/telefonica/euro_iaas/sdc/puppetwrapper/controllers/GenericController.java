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

package com.telefonica.euro_iaas.sdc.puppetwrapper.controllers;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.ModuleDownloaderException;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.PuppetWrapperError;

public class GenericController {

    private static final Logger LOG = LoggerFactory.getLogger(GenericController.class);

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public PuppetWrapperError handleNoSuchElementException(NoSuchElementException ex) {
        LOG.error(ex.getMessage());
        return new PuppetWrapperError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler(ModuleDownloaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public PuppetWrapperError handleModuleDownloaderException(ModuleDownloaderException ex) {
        LOG.error(ex.getMessage());
        return new PuppetWrapperError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
    
    
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public PuppetWrapperError handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        LOG.error(ex.getMessage());
        return new PuppetWrapperError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), ex.getMessage());
    }

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public PuppetWrapperError handleException(Exception ex) {
        LOG.error(ex.getMessage());
        return new PuppetWrapperError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

}
