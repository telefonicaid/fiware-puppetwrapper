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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.openstack.docs.identity.api.v2.AuthenticateResponse;
import org.openstack.docs.identity.api.v2.TenantForAuthenticateResponse;
import org.openstack.docs.identity.api.v2.Token;
import org.openstack.docs.identity.api.v2.UserForAuthenticateResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.telefonica.euro_iaas.sdc.puppetwrapper.exception.AuthenticationConnectionException;

public class OpenStackAuthenticationProviderTest {

    OpenStackAuthenticationToken openStackAuthenticationToken;

    @Before
    public void setup() {

    }

    @Test
    public void shouldCreatesNewTokenAndValidateWhenAdminTokenIsNotAuthorized()
            throws AuthenticationConnectionException {
        // given

        String adminToken = "fa63ea912a2a579c2542dbb90951d500";

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationProvider.setAdminPass("admin");
        openStackAuthenticationProvider.setAdminTenant("00000000000000000000000000000001");
        openStackAuthenticationProvider.setAdminUser("admin");
        openStackAuthenticationProvider.setKeystoneURL("http://keystone.test");
        openStackAuthenticationProvider.setThresholdString("1");
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.oSAuthToken = openStackAuthenticationToken;
        Client client = mock(Client.class);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        WebTarget webResource2 = mock(WebTarget.class);
        Invocation.Builder builder2 = mock(Invocation.Builder.class);
        Response clientResponse = mock(Response.class);
        Response response401 = mock(Response.class);
        AuthenticateResponse authenticateResponse = mock(AuthenticateResponse.class);

        // when
        when(openStackAuthenticationToken.getCredentials()).thenReturn(new String[] { adminToken, "string2" });
        when(client.target("http://keystone.test")).thenReturn(webResource).thenReturn(webResource2);
        when(webResource.path(any(String.class))).thenReturn(webResource);
        when(webResource.request()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.header(eq("X-Auth-Token"), anyString())).thenReturn(builder);
        when(builder.get()).thenReturn(response401);
        when(response401.getStatus()).thenReturn(401);

        when(webResource2.path(any(String.class))).thenReturn(webResource2);
        when(webResource2.request()).thenReturn(builder2);
        when(builder2.header(anyString(), anyString())).thenReturn(builder2);
        when(builder2.header(eq("X-Auth-Token"), anyString())).thenReturn(builder2);
        when(builder2.get(AuthenticateResponse.class)).thenReturn(authenticateResponse);

        Token validToken = mock(Token.class);
        TenantForAuthenticateResponse tenantForAuthenticateResponse = mock(TenantForAuthenticateResponse.class);
        UserForAuthenticateResponse userForAuthenticateResponse = mock(UserForAuthenticateResponse.class);
        when(authenticateResponse.getToken()).thenReturn(validToken);
        when(validToken.getTenant()).thenReturn(tenantForAuthenticateResponse);
        when(tenantForAuthenticateResponse.getId()).thenReturn("user tenantId");
        when(authenticateResponse.getUser()).thenReturn(userForAuthenticateResponse);
        when(userForAuthenticateResponse.getRoles()).thenReturn(null);
        Map<QName, String> collection = new HashMap<QName, String>();
        collection.put(QName.valueOf("username"), "value");
        when(userForAuthenticateResponse.getOtherAttributes()).thenReturn(collection);

        PaasManagerUser paasManagerUser = openStackAuthenticationProvider.authenticationFiware("user token",
                "user tenantId");

        // then
        assertNotNull(paasManagerUser);
        verify(openStackAuthenticationToken, times(2)).getCredentials();
    }

    @Test
    public void retrieveUserTest_systemFiware() throws AuthenticationConnectionException {
        String adminToken = "fa63ea912a2a579c2542dbb90951d500";

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationProvider.setAdminPass("admin");
        openStackAuthenticationProvider.setAdminTenant("00000000000000000000000000000001");
        openStackAuthenticationProvider.setAdminUser("admin");
        openStackAuthenticationProvider.setKeystoneURL("http://keystone.test");
        openStackAuthenticationProvider.setThresholdString("1");
        openStackAuthenticationProvider.setCloudSystem("FIWARE");
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.oSAuthToken = openStackAuthenticationToken;
        Client client = mock(Client.class);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        WebTarget webResource2 = mock(WebTarget.class);
        Invocation.Builder builder2 = mock(Invocation.Builder.class);
        Response clientResponse = mock(Response.class);
        Response response401 = mock(Response.class);
        AuthenticateResponse authenticateResponse = mock(AuthenticateResponse.class);

        // when
        when(openStackAuthenticationToken.getCredentials()).thenReturn(new String[] { adminToken, "string2" });
        when(client.target("http://keystone.test")).thenReturn(webResource).thenReturn(webResource2);
        when(webResource.path(any(String.class))).thenReturn(webResource);
        when(webResource.request()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.header(eq("X-Auth-Token"), anyString())).thenReturn(builder);
        when(builder.get()).thenReturn(response401);
        when(response401.getStatus()).thenReturn(401);

        when(webResource2.path(any(String.class))).thenReturn(webResource2);
        when(webResource2.request()).thenReturn(builder2);
        when(builder2.header(anyString(), anyString())).thenReturn(builder2);
        when(builder2.header(eq("X-Auth-Token"), anyString())).thenReturn(builder2);
        when(builder2.get(AuthenticateResponse.class)).thenReturn(authenticateResponse);

        Token validToken = mock(Token.class);
        TenantForAuthenticateResponse tenantForAuthenticateResponse = mock(TenantForAuthenticateResponse.class);
        UserForAuthenticateResponse userForAuthenticateResponse = mock(UserForAuthenticateResponse.class);
        when(authenticateResponse.getToken()).thenReturn(validToken);
        when(validToken.getTenant()).thenReturn(tenantForAuthenticateResponse);
        when(tenantForAuthenticateResponse.getId()).thenReturn("user tenantId");
        when(authenticateResponse.getUser()).thenReturn(userForAuthenticateResponse);
        when(userForAuthenticateResponse.getRoles()).thenReturn(null);
        Map<QName, String> collection = new HashMap<QName, String>();
        collection.put(QName.valueOf("username"), "value");
        when(userForAuthenticateResponse.getOtherAttributes()).thenReturn(collection);

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");

        UserDetails user = openStackAuthenticationProvider.retrieveUser("pepe", authentication);

        assertNotNull(user);
        verify(openStackAuthenticationToken, times(2)).getCredentials();
        verify(authentication, times(2)).getCredentials();

    }

    @Test
    public void retrieveUserTest_systemFasttrack() throws AuthenticationConnectionException {
        String adminToken = "fa63ea912a2a579c2542dbb90951d500";

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationProvider.setAdminPass("admin");
        openStackAuthenticationProvider.setAdminTenant("00000000000000000000000000000001");
        openStackAuthenticationProvider.setAdminUser("admin");
        openStackAuthenticationProvider.setKeystoneURL("http://keystone.test");
        openStackAuthenticationProvider.setThresholdString("1");
        openStackAuthenticationProvider.setCloudSystem("FASTTRACK");
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.oSAuthToken = openStackAuthenticationToken;
        Client client = mock(Client.class);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        WebTarget webResource2 = mock(WebTarget.class);
        Invocation.Builder builder2 = mock(Invocation.Builder.class);
        Response clientResponse = mock(Response.class);
        Response response401 = mock(Response.class);
        AuthenticateResponse authenticateResponse = mock(AuthenticateResponse.class);

        // when
        when(openStackAuthenticationToken.getCredentials()).thenReturn(new String[] { adminToken, "string2" });
        when(client.target("http://keystone.test")).thenReturn(webResource).thenReturn(webResource2);
        when(webResource.path(any(String.class))).thenReturn(webResource);
        when(webResource.request()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.header(eq("X-Auth-Token"), anyString())).thenReturn(builder);
        when(builder.get()).thenReturn(response401);
        when(response401.getStatus()).thenReturn(401);

        when(webResource2.path(any(String.class))).thenReturn(webResource2);
        when(webResource2.request()).thenReturn(builder2);
        when(builder2.header(anyString(), anyString())).thenReturn(builder2);
        when(builder2.header(eq("X-Auth-Token"), anyString())).thenReturn(builder2);
        when(builder2.get(AuthenticateResponse.class)).thenReturn(authenticateResponse);

        Token validToken = mock(Token.class);
        TenantForAuthenticateResponse tenantForAuthenticateResponse = mock(TenantForAuthenticateResponse.class);
        UserForAuthenticateResponse userForAuthenticateResponse = mock(UserForAuthenticateResponse.class);
        when(authenticateResponse.getToken()).thenReturn(validToken);
        when(validToken.getTenant()).thenReturn(tenantForAuthenticateResponse);
        when(tenantForAuthenticateResponse.getId()).thenReturn("user tenantId");
        when(authenticateResponse.getUser()).thenReturn(userForAuthenticateResponse);
        when(userForAuthenticateResponse.getRoles()).thenReturn(null);
        Map<QName, String> collection = new HashMap<QName, String>();
        collection.put(QName.valueOf("username"), "value");
        when(userForAuthenticateResponse.getOtherAttributes()).thenReturn(collection);

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");

        UserDetails user = openStackAuthenticationProvider.retrieveUser("pepe", authentication);

        assertTrue(user == null);
        verify(authentication, times(2)).getCredentials();

    }
}
