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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.apache.http.impl.client.DefaultHttpClient;
import org.openstack.docs.identity.api.v2.AuthenticateResponse;
import org.openstack.docs.identity.api.v2.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import com.telefonica.euro_iaas.sdc.puppetwrapper.exception.AuthenticationConnectionException;

/**
 * The Class OpenStackAuthenticationProvider.
 */
public class OpenStackAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * The Constant SYSTEM_FIWARE.
     */
    public static final String SYSTEM_FIWARE = "FIWARE";
    /**
     * The Constant SYSTEM_FASTTRACK.
     */
    public static final String SYSTEM_FASTTRACK = "FASTTRACK";

    /**
     * The Constant CODE_200.
     */
    public static final int CODE_200 = 200;

    /**
     * The Constant CODE_401.
     */
    public static final int CODE_401 = 401;
    /**
     * The Constant CODE_403.
     */
    public static final int CODE_403 = 403;
    /**
     * The Constant CODE_404.
     */
    public static final int CODE_404 = 404;
    /**
     * The max number of reintent.
     */
    public static final int MAX_REINTENT = 5;
    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(OpenStackAuthenticationProvider.class);
    /**
     * Thread to recover a valid X-Auth-Token each 24 hour.
     */
    OpenStackAuthenticationToken oSAuthToken = null;

    /**
     * + * Jersey client used to validates token to OpenStack. +
     */
    private Client client;

    private String keystoneURL = "";

    private String adminUser = "";

    private String adminPass = "";

    private String adminTenant = "";

    private String thresholdString = "";

    private String cloudSystem = "";

    /**
     * + * Default constructor.
     */
    public OpenStackAuthenticationProvider() {
        client = ClientBuilder.newClient();
        ;
    }

    /*
     * (non-Javadoc) @seeorg.springframework.security.authentication.dao.
     * AbstractUserDetailsAuthenticationProvider
     * #additionalAuthenticationChecks(
     * org.springframework.security.core.userdetails.UserDetails,
     * org.springframework
     * .security.authentication.UsernamePasswordAuthenticationToken)
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) {
    }

    /**
     * Authentication fast track.
     * 
     * @param username
     *            the username
     * @param tenantId
     *            the tenantId
     * @return the open stack user
     */
    private PaasManagerUser authenticationFastTrack(String username, String tenantId) {
        return null;

    }

    /**
     * Authentication fiware.
     * 
     * @param token
     *            the token
     * @param tenantId
     *            the tenantId
     * @return the open stack user
     * @throws AuthenticationConnectionException
     */
    @SuppressWarnings("deprecation")
    public PaasManagerUser authenticationFiware(String token, String tenantId) throws AuthenticationConnectionException {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        configureOpenStackAuthenticationToken(keystoneURL, adminUser, adminPass, adminTenant, thresholdString,
                httpClient);

        String[] credential = oSAuthToken.getCredentials();

        log.info("Keystone URL : " + keystoneURL);
        log.info("adminToken : " + credential[0]);

        WebTarget webResource = client.target(keystoneURL);
        try {

            Response response = webResource.path("tokens").path(token).request().header("Accept", "application/xml")
                    .header("X-Auth-Token", credential[0]).get();

            if (response.getStatus() == CODE_200) {
                AuthenticateResponse authenticateResponse = response.readEntity(AuthenticateResponse.class);

                // Validate user's token
                return validateUserToken(token, tenantId, authenticateResponse);
            } else if (response.getStatus() == CODE_401) {

                // create new admin token
                configureOpenStackAuthenticationToken(keystoneURL, adminUser, adminPass, adminTenant, thresholdString,
                        httpClient);
                String[] newCredentials = oSAuthToken.getCredentials();
                // try validateUserToken
                WebTarget webResource2 = client.target(keystoneURL);
                return validateUserToken(
                        token,
                        tenantId,
                        webResource2.path("tokens").path(token).request().header("Accept", "application/xml")
                                .header("X-Auth-Token", newCredentials[0]).get(AuthenticateResponse.class));

            } else if ((response.getStatus() == CODE_403) || (response.getStatus() == CODE_404)) {
                throw new BadCredentialsException("Token not valid");
            }
            throw new AuthenticationServiceException("Token not valid");

        } catch (Exception e) {

            throw new AuthenticationServiceException("unknown problem", e);
        }
    }

    /**
     * + * Connect to keystone and validate user token using admin token. + * +
     * * @param token + * @param tenantId + * @param authenticateResponse + * @return
     * +
     */
    private PaasManagerUser validateUserToken(String token, String tenantId, AuthenticateResponse authenticateResponse) {
        AuthenticateResponse responseAuth = authenticateResponse;

        if (!tenantId.equals(responseAuth.getToken().getTenant().getId())) {
            throw new AuthenticationServiceException("Token " + responseAuth.getToken().getTenant().getId()
                    + " not valid for the tenantId provided:" + tenantId);
        }

        Set<GrantedAuthority> authsSet = new HashSet<GrantedAuthority>();

        if (responseAuth.getUser().getRoles() != null) {
            for (Role role : responseAuth.getUser().getRoles().getRole()) {
                authsSet.add(new GrantedAuthorityImpl(role.getName()));
            }
        }

        PaasManagerUser user = new PaasManagerUser(responseAuth.getUser().getOtherAttributes()

        .get(new QName("username")), token, authsSet);
        user.setTenantId(tenantId);
        user.setTenantName(responseAuth.getToken().getTenant().getName());
        user.setToken(token);
        return user;
    }

    private void configureOpenStackAuthenticationToken(String keystoneURL, String adminUser, String adminPass,
            String adminTenant, String thresholdString, DefaultHttpClient httpClient) {
        ArrayList<Object> params = new ArrayList();

        Long threshold = Long.parseLong(thresholdString);

        params.add(keystoneURL);
        params.add(adminTenant);
        params.add(adminUser);
        params.add(adminPass);
        params.add(httpClient);
        params.add(threshold);

        if (oSAuthToken == null) {
            oSAuthToken = new OpenStackAuthenticationToken(params);
        } else {
            oSAuthToken.initialize(params);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /*
     * (non-Javadoc) @seeorg.springframework.security.authentication.dao.
     * AbstractUserDetailsAuthenticationProvider #retrieveUser(java.lang.String,
     * org
     * .springframework.security.authentication.UsernamePasswordAuthenticationToken
     * )
     */
    @Override
    protected final UserDetails retrieveUser(final String username,
            final UsernamePasswordAuthenticationToken authentication) {

        PaasManagerUser user = null;
        String tenantId = null;
        if (null != authentication.getCredentials()) {
            tenantId = authentication.getCredentials().toString();

            if (SYSTEM_FIWARE.equals(cloudSystem)) {
                try {
                    user = authenticationFiware(username, tenantId);
                } catch (AuthenticationConnectionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (SYSTEM_FASTTRACK.equals(cloudSystem)) {
                user = authenticationFastTrack(username, tenantId);
            }
        } else {
            String str = "Missing tenantId header";
            log.info(str);
            throw new BadCredentialsException(str);
        }

        return user;
    }

    @Value(value = "${keystoneURL}")
    public void setKeystoneURL(String keystoneURL) {
        this.keystoneURL = keystoneURL;
    }

    @Value(value = "${adminUser}")
    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    @Value(value = "${adminPass}")
    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    @Value(value = "${adminTenant}")
    public void setAdminTenant(String adminTenant) {
        this.adminTenant = adminTenant;
    }

    @Value(value = "${thresholdString}")
    public void setThresholdString(String thresholdString) {
        this.thresholdString = thresholdString;
    }

    @Value(value = "${cloudSystem}")
    public void setCloudSystem(String cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

}
