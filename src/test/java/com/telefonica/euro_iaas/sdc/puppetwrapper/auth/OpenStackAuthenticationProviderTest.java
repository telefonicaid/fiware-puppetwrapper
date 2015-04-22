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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.telefonica.fiware.commons.openstack.auth.OpenStackAccess;
import com.telefonica.fiware.commons.openstack.auth.OpenStackAuthenticationToken;
import com.telefonica.fiware.commons.openstack.auth.OpenStackKeystoneV2;
import com.telefonica.fiware.commons.openstack.auth.OpenStackKeystoneV3;

/**
 * Test class to check the OpenStackAuthenticationProvider.
 */
public class OpenStackAuthenticationProviderTest {

    private final String keystoneURL = "http://keystone.test";

    private OpenStackAuthenticationToken openStackAuthenticationToken;

    @Test
    public void shouldCreatesNewTokenForAdminAndUser() {

        // Given

        String responseJSON = "{\"token\":{\"methods\":[\"password\"],"
                + "\"roles\":[{\"id\":\"13abab31bc194317a009b25909f390a6\",\"name\":\"owner\"}],"
                + "\"expires_at\":\"2015-04-16T06:49:07.794235Z\",\"project\":{\"domain\":{\"id\":\"default\","
                + "\"name\":\"Default\"},\"id\":\"user tenantId\",\"name\":\"jesuspg2\"},"
                + "\"extras\":{},\"user\":{\"domain\":{\"id\":\"default\",\"name\":\"Default\"},"
                + "\"id\":\"a7e01921db0049f69daa76490402714a\",\"name\":\"jesus.perezgonzalez@telefonica.com\"},"
                + "\"audit_ids\":[\"0u8bgE6AStObXnzfI9nu6A\"],\"issued_at\":\"2015-04-15T10:49:07.794329Z\"}}";

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();

        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.setoSAuthToken(openStackAuthenticationToken);
        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("token1");
        openStackAccess.setTenantId("tenantId1");
        openStackAccess.setOpenStackKeystone(new OpenStackKeystoneV3());

        when(openStackAuthenticationToken.getAdminCredentials(any(Client.class))).thenReturn(openStackAccess);
        Client client = mock(Client.class);
        when(openStackAuthenticationToken.getKeystoneURL()).thenReturn(keystoneURL);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        when(client.target("http://keystone.test")).thenReturn(webResource);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(webResource.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.header("X-Auth-Token", "token1")).thenReturn(builder);
        when(builder.header("X-Subject-Token", "user token")).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(200);

        // mock response
        when(response.readEntity(String.class)).thenReturn(responseJSON);

        openStackAuthenticationProvider.getTokenCache().removeAll();

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");

        // When
        UserDetails userDetails = openStackAuthenticationProvider.retrieveUser("user token", authentication);

        // Then
        verify(response).readEntity(String.class);
        assertNotNull(userDetails);
        assertEquals("user token", userDetails.getPassword());

    }

    @Test
    public void shouldCreatesNewTokenForAdminAndUserWithAPIv3() {

        // Given

        String responseJSON = "{\"access\": {\"token\": {\"issued_at\": \"2015-04-16T14:47:17.573966\", "
                + "\"expires\": \"2015-04-17T10:47:17Z\", \"id\": \"user token\", "
                + "\"tenant\": {\"description\": \"Cloud admins\", \"enabled\": true, "
                + "\"id\": \"user tenantId\", \"name\": \"tenantName\"}, "
                + "\"audit_ids\": [\"z4fSnIPsQ2eu3ylzoXRfvA\"]}, \"user\": {\"username\": \"admin\", "
                + "\"roles_links\": [], \"id\": \"e12249b99b3e4b9394dd85703b04e851\", "
                + "\"roles\": [{\"name\": \"admin\"}], \"name\": \"admin\"}, \"metadata\": {\"is_admin\": 0, "
                + "\"roles\": [\"bb780354f545410b9cc144809e845148\"]}}}";

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.setoSAuthToken(openStackAuthenticationToken);
        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("token1");
        openStackAccess.setTenantId("tenantId1");
        openStackAccess.setOpenStackKeystone(new OpenStackKeystoneV2());

        when(openStackAuthenticationToken.getAdminCredentials(any(Client.class))).thenReturn(openStackAccess);
        Client client = mock(Client.class);
        when(openStackAuthenticationToken.getKeystoneURL()).thenReturn(keystoneURL);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        when(client.target("http://keystone.test")).thenReturn(webResource);
        when(webResource.path("user token")).thenReturn(webResource);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(webResource.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.header("X-Auth-Token", "token1")).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(200);

        // mock response
        when(response.readEntity(String.class)).thenReturn(responseJSON);

        openStackAuthenticationProvider.getTokenCache().removeAll();

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");

        // When
        UserDetails userDetails = openStackAuthenticationProvider.retrieveUser("user token", authentication);

        // Then
        verify(response).readEntity(String.class);
        assertNotNull(userDetails);
        assertEquals("user token", userDetails.getPassword());

    }

    @Test
    public void shouldCreateNewTokenAfterResetCache() throws InterruptedException {
        // Given

        String responseJSON = "{\"token\":{\"methods\":[\"password\"],"
                + "\"roles\":[{\"id\":\"13abab31bc194317a009b25909f390a6\",\"name\":\"owner\"}],"
                + "\"expires_at\":\"2015-04-16T06:49:07.794235Z\",\"project\":{\"domain\":{\"id\":\"default\","
                + "\"name\":\"Default\"},\"id\":\"user tenantId\",\"name\":\"jesuspg2\"},"
                + "\"extras\":{},\"user\":{\"domain\":{\"id\":\"default\",\"name\":\"Default\"},"
                + "\"id\":\"a7e01921db0049f69daa76490402714a\",\"name\":\"jesus.perezgonzalez@telefonica.com\"},"
                + "\"audit_ids\":[\"0u8bgE6AStObXnzfI9nu6A\"],\"issued_at\":\"2015-04-15T10:49:07.794329Z\"}}";

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.setoSAuthToken(openStackAuthenticationToken);

        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("token1");
        openStackAccess.setTenantId("tenantId1");
        openStackAccess.setOpenStackKeystone(new OpenStackKeystoneV3());

        when(openStackAuthenticationToken.getAdminCredentials(any(Client.class))).thenReturn(openStackAccess);
        when(openStackAuthenticationToken.getKeystoneURL()).thenReturn(keystoneURL);
        Client client = mock(Client.class);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        when(client.target("http://keystone.test")).thenReturn(webResource);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(webResource.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.header("X-Auth-Token", "token1")).thenReturn(builder);
        when(builder.header("X-Subject-Token", "user token")).thenReturn(builder);

        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(200);

        // mock response
        when(response.readEntity(String.class)).thenReturn(responseJSON);

        openStackAuthenticationProvider.getTokenCache().removeAll();
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");
        // When
        UserDetails firstTimeUserDetails = openStackAuthenticationProvider.retrieveUser("user token", authentication);

        // force expire elements now
        openStackAuthenticationProvider.getTokenCache().get("admin").setTimeToIdle(1);
        openStackAuthenticationProvider.getTokenCache().get("admin").setTimeToLive(1);
        openStackAuthenticationProvider.getTokenCache().get("user token-user tenantId").setTimeToIdle(1);
        openStackAuthenticationProvider.getTokenCache().get("user token-user tenantId").setTimeToLive(1);
        Thread.sleep(2000);

        UserDetails secondTimeUserDetails = openStackAuthenticationProvider.retrieveUser("user token", authentication);

        // Then
        verify(response, times(2)).readEntity(String.class);
        assertNotNull(firstTimeUserDetails);
        assertEquals("user token", firstTimeUserDetails.getPassword());

        assertEquals("user token", secondTimeUserDetails.getPassword());
    }

    @Test(expected = AuthenticationServiceException.class)
    public void shouldReturnErrorWithFailInOpenStack() {

        // Given

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.setoSAuthToken(openStackAuthenticationToken);
        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("token1");
        openStackAccess.setTenantId("tenantId1");

        when(openStackAuthenticationToken.getAdminCredentials(any(Client.class))).thenReturn(openStackAccess);
        Client client = mock(Client.class);
        when(openStackAuthenticationToken.getKeystoneURL()).thenReturn(keystoneURL);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        when(client.target("http://keystone.test")).thenReturn(webResource);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(webResource.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.header("X-Auth-Token", "token1")).thenReturn(builder);
        when(builder.header("X-Subject-Token", "user token")).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(500);

        // mock response
        openStackAuthenticationProvider.getTokenCache().removeAll();

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");

        // When
        openStackAuthenticationProvider.retrieveUser("user token", authentication);

        // Then
        verify(response).getStatus();

    }

    @Test(expected = AuthenticationServiceException.class)
    public void shouldReturnErrorWithInvalidToken() {

        // Given

        OpenStackAuthenticationProvider openStackAuthenticationProvider = new OpenStackAuthenticationProvider();
        openStackAuthenticationToken = mock(OpenStackAuthenticationToken.class);
        openStackAuthenticationProvider.setoSAuthToken(openStackAuthenticationToken);
        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("token1");
        openStackAccess.setTenantId("tenantId1");

        when(openStackAuthenticationToken.getAdminCredentials(any(Client.class))).thenReturn(openStackAccess);
        Client client = mock(Client.class);
        when(openStackAuthenticationToken.getKeystoneURL()).thenReturn(keystoneURL);
        openStackAuthenticationProvider.setClient(client);
        WebTarget webResource = mock(WebTarget.class);
        when(client.target("http://keystone.test")).thenReturn(webResource);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(webResource.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.header("X-Auth-Token", "token1")).thenReturn(builder);
        when(builder.header("X-Subject-Token", "user token")).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(401);

        // mock response
        openStackAuthenticationProvider.getTokenCache().removeAll();

        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getCredentials()).thenReturn("user tenantId");

        // When
        openStackAuthenticationProvider.retrieveUser("user token", authentication);

        // Then
        verify(response).getStatus();

    }
}
