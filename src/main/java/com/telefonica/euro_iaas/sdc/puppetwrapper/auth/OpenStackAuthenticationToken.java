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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.sdc.puppetwrapper.exception.AuthenticationConnectionException;

/**
 * @author fernandolopezaguilar
 */
public class OpenStackAuthenticationToken {

    /**
     * The log.
     */
    // private Timer timer;
    /**
     * The token ID.
     */
    private String token;
    /**
     * The tenant ID.
     */
    private String tenantId;
    /**
     * The expiration date of the token.
     */
    private static String date;
    /**
     * The url of the keystone service.
     */
    private String url;
    /**
     * The tenant name.
     */
    private String tenant;
    /**
     * The user of the keystone admin.
     */
    private String user;
    /**
     * The pass of the keystone admin.
     */
    private String pass;
    private HttpClient httpClient;
    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(OpenStackAuthenticationToken.class);
    /**
     * The limit to request a new token due to it is not more valid. This means
     * that the token is no more valid after 6m 40sec.
     */
    private long threshold;
    /**
     * The Offset between the local time and the remote Date
     */
    private static long offset;

    OpenStackAuthenticationToken(ArrayList<Object> params) {

        initialize(params);
    }

    public void initialize(ArrayList<Object> params) {
        this.token = "";
        this.tenantId = "";
        this.url = (String) params.get(0);
        this.tenant = (String) params.get(1);
        this.user = (String) params.get(2);
        this.pass = (String) params.get(3);

        this.httpClient = (HttpClient) params.get(4);

        this.threshold = (Long) params.get(5);

        offset = 0;
    }

    public String[] getCredentials() throws AuthenticationConnectionException {
        String[] credential = new String[2];

        CompareDates compare = new CompareDates(this.threshold);

        compare.setOffset(offset);

        log.info("Offset: " + offset);

        if (token.equals("")) {
            generateValidToken();
        } else {
            if (compare.checkDate(date, new Date())) {
                generateValidToken();
            }
        }

        credential[0] = token;
        credential[1] = tenantId;

        return credential;
    }

    private void generateValidToken() throws AuthenticationConnectionException {
        HttpPost postRequest = createKeystonePostRequest();
        ArrayList<Object> response = executePostRequest(postRequest);
        extractData(response);
    }

    protected void extractData(ArrayList<Object> response) {
        String payload = (String) response.get(0);

        int i = payload.indexOf("token");
        int j = payload.indexOf(">", i);
        token = payload.substring(i - 1, j + 1);

        // token =
        // "<token expires=\"2012-11-13T15:01:51Z\" id=\"783bec9d7d734f1e943986485a90966d\">";
        // Regular Expression
        // <\s*token\s*(issued_at=\".*?\"\s*)?expires=\"(.*?)(\"\s*id=\")(.*)\"\/*>
        // as a Java string
        // "<\\s*token\\s*(issued_at=\\\".*?\\\"\\s*)?expires=\\\"(.*?)(\\\"\\s*id=\\\")(.*)\\\"\\/*>"
        String pattern1 = "<\\s*token\\s*(issued_at=\\\".*?\\\"\\s*)?expires=\\\"(.*?)(\\\"\\s*id=\\\")(.*)\\\"\\/*>";

        if (token.matches(pattern1)) {
            date = token.replaceAll(pattern1, "$2");
            token = token.replaceAll(pattern1, "$4");

            log.info("Valid to: " + date);
            log.info("token id: " + token);
        } else {
            log.error("Token format unknown: " + token);

            throw new RuntimeException("Token format unknown:\n " + token);
        }

        i = payload.indexOf("tenant");
        j = payload.indexOf(">", i);
        tenantId = payload.substring(i - 1, j + 1);

        if (tenantId == null || tenantId.isEmpty()) {
            log.error("Tenant format unknown:\n " + tenantId);
            throw new RuntimeException("Tenant format unknown:\n " + tenantId);
        }

        // Regular Expression (<\s*tenant\s*.*)("\s*id=")(.*?)("\s*.*/*>)
        // as a Java string "(<\\s*tenant\\s*.*)(\"\\s*id=\")(.*?)(\"\\s*.*/*>)"
        pattern1 = "(<\\s*tenant\\s*.*)(\"\\s*id=\")(.*?)(\"\\s*.*/*>)";

        if (tenantId.matches(pattern1)) {
            tenantId = tenantId.replaceAll(pattern1, "$3");
        } else {
            log.error("Tenant format unknown:\n " + tenantId);

            throw new RuntimeException("Tenant format unknown:\n " + tenantId);
        }

        CompareDates compare = new CompareDates();

        date = compare.validateDates(date, (String) response.get(1));

        offset = compare.getTimeDiff((String) response.get(1), (Date) response.get(2));

        log.info("tenant id: " + tenantId);
        log.info("Offset time: " + offset);
    }

    public long getOffset() {
        return offset;
    }

    public String getDate() {
        return date;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getToken() {
        return token;
    }

    private HttpPost createKeystonePostRequest() throws AuthenticationConnectionException {
        // curl -d '{"auth": {"tenantName": "demo", "passwordCredentials":
        // {"username": "admin", "password": "temporal"}}}'
        // -H "Content-type: application/json"
        // -H "Accept: application/xml"�
        // http://10.95.171.115:35357/v2.0/tokens

        HttpEntity entity = null;
        HttpPost postRequest = new HttpPost(url + "tokens");

        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Accept", "application/xml");

        String msg = "{\"auth\": {\"tenantName\": \"" + tenant + "\", \"" + "passwordCredentials\":{\"username\": \""
                + user + "\"," + " \"password\": \"" + pass + "\"}}}";

        try {
            entity = new StringEntity(msg);
        } catch (UnsupportedEncodingException ex) {
            log.error("Unsupported encoding exception");

            throw new AuthenticationConnectionException("Unsupported encoding exception");
        }

        postRequest.setEntity(entity);

        return postRequest;
    }

    private ArrayList<Object> executePostRequest(HttpPost postRequest) throws AuthenticationConnectionException {
        HttpResponse response;
        httpClient = new DefaultHttpClient();

        ArrayList<Object> message = new ArrayList();

        Date localDate = null;

        String aux;

        try {
            response = httpClient.execute(postRequest);

            localDate = new Date();

            if ((response.getStatusLine().getStatusCode() != 201) && (response.getStatusLine().getStatusCode() != 200)) {

                String exceptionMessage = "Failed : HTTP error code : (" + postRequest.getURI().toString() + ")"
                        + response.getStatusLine().getStatusCode() + " message: " + response;
                log.error(exceptionMessage);

                throw new RuntimeException(exceptionMessage);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String temp = "";

            while ((aux = br.readLine()) != null) {
                temp += aux;
            }

            message.add(temp);

            String aux1 = response.getHeaders("Date")[0].getValue();
            log.info("Date recibido: " + aux1);
            message.add(response.getHeaders("Date")[0].getValue());

            HttpEntity ent = response.getEntity();
            if (ent != null) {
                EntityUtils.consume(ent);
            }

        } catch (ClientProtocolException ex) {
            log.error("Client protocol exception");

            throw new AuthenticationConnectionException("Client protocol exception");
        } catch (IOException ex) {
            log.error("I/O exception of some sort has occurred");

            throw new AuthenticationConnectionException("I/O exception of some sort has occurred");
        }

        // Calculate the offset between the local date and the remote date
        log.info("Date local: " + localDate);
        message.add(localDate);

        return message;
    }
}
