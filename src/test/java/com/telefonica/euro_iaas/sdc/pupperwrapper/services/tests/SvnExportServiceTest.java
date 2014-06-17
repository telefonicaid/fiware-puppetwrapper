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

package com.telefonica.euro_iaas.sdc.pupperwrapper.services.tests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.ModuleDownloaderException;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl.SvnExporterServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:testContext.xml" })
@Ignore
public class SvnExportServiceTest {
    
    private SvnExporterServiceImpl svnExporterService;
    
    @Value("${modulesCodeDownloadPath}")
    private String modulesCodeDownloadPath;
    
    @Before
    public void setUp(){
        svnExporterService=new SvnExporterServiceImpl();
        svnExporterService.setModulesCodeDownloadPath(modulesCodeDownloadPath);
    }
    
    @Test
    public void exportTest() throws ModuleDownloaderException{
        
        svnExporterService.download("https://forge.fi-ware.eu/scmrepos/svn/testbed/trunk/cookbooks/GESoftware/beatest", "moduleTest");
        
    }

}
