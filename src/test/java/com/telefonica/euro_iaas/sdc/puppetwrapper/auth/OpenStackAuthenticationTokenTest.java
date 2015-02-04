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
package com.telefonica.euro_iaas.sdc.puppetwrapper.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.sdc.puppetwrapper.exception.AuthenticationConnectionException;

public class OpenStackAuthenticationTokenTest {

    OpenStackAuthenticationToken openStackAuthenticationToken;
    ArrayList<Object> params = new ArrayList<Object>();
    HttpClient httpClient;
    HttpResponse response;
    StatusLine statusLine;
    HttpEntity httpEntity;
    InputStream is;

    private String payload = "<access xmlns=\"http://docs.openstack.org/identity/api/v2.0\"><token "
            + "expires=\"2015-07-09T15:16:07Z\" id=\"35b208abaf09707c5fed8e54af9a48b8\"><tenant "
            + "enabled=\"true\" id=\"00000000000000000000000000000001\" name=\"00000000000000000000000000000001\"/>"
            + "</token><serviceCatalog><endpoints><adminURL>http://130.206.80.58:8774/v2/undefined</adminURL>"
            + "<region>Trento</region><internalURL>http://130.206.80.58:8774/v2/undefined</internalURL>";

    @Before
    public void setup() throws IOException {

        httpClient = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        statusLine = mock(StatusLine.class);
        httpEntity = mock(HttpEntity.class);
        is = IOUtils.toInputStream(payload, "UTF-8");

        params.add("url");
        params.add("tenant");
        params.add("user");
        params.add("passw");
        params.add(httpClient);
        params.add(new Long(3));

        openStackAuthenticationToken = new OpenStackAuthenticationToken(params);
    }

    @Test
    public void getCredentialsTest() throws AuthenticationConnectionException, ClientProtocolException, IOException {
        Header header = new Header() {

            @Override
            public String getValue() {
                return "Fri, 21 Nov 2014 12:30:54 GMT";
            }

            @Override
            public String getName() {
                return "Date";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                // TODO Auto-generated method stub
                return null;
            }
        };
        Header[] headers = new Header[] { header };

        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(is);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getHeaders(anyString())).thenReturn(headers);
//        when(response.get(1)).ther
        
//        2015-07-09T15:16:07Z

        openStackAuthenticationToken.getCredentials();
        
        verify(httpClient,times(1)).execute(any(HttpPost.class));

    }
}
