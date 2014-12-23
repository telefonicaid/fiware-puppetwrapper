__author__ = 'jfernandez'

import json
from rest_utils import RestUtils
from configuration import CONFIG_KEYSTONE_TENANT_NAME_VALUE, \
    CONFIG_KEYSTONE_USERNAME_VALUE, CONFIG_KEYSTONE_PWD_VALUE
from constants import AUTH_TENANT_NAME, AUTH_PASSWORD, AUTH_USERNAME, AUTH, AUTH_ACCESS, AUTH_TENANT, AUTH_TOKEN, \
    AUTH_ID, AUTH_PASSWORD_CREDENTIALS, CONTENT_TYPE, CONTENT_TYPE_JSON, ACCEPT_HEADER, ACCEPT_HEADER_JSON

KEYSTONE_BODY = {AUTH: {AUTH_TENANT_NAME: CONFIG_KEYSTONE_TENANT_NAME_VALUE,
                        AUTH_PASSWORD_CREDENTIALS: {AUTH_USERNAME: CONFIG_KEYSTONE_USERNAME_VALUE,
                                                    AUTH_PASSWORD: CONFIG_KEYSTONE_PWD_VALUE}}}

CONFIG_KEYSTONE_HEADERS = {CONTENT_TYPE: CONTENT_TYPE_JSON, ACCEPT_HEADER: ACCEPT_HEADER_JSON}


def get_auth_data_from_keystone():
    body = json.dumps(KEYSTONE_BODY)
    r = RestUtils.get_keystone_token(body=body, headers=CONFIG_KEYSTONE_HEADERS)
    response = r.json()
    token_id = response[AUTH_ACCESS][AUTH_TOKEN][AUTH_ID]
    tenant_id = response[AUTH_ACCESS][AUTH_TOKEN][AUTH_TENANT][AUTH_ID]
    return token_id, tenant_id
