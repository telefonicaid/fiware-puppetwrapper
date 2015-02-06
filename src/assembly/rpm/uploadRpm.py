__author__ = 'jmmovilla'

import requests
import json
import os
import sys
import ntpath
import subprocess

#AUTHENTICATION CONSTANTS
AUTH = u'auth'
TENANT_NAME = u'tenantName'
USERNAME = u'username'
PASSWORD = u'password'
PASSWORD_CREDENTIALS = u'passwordCredentials'
ACCESS = u'access'
TOKEN = u'token'
TENANT = u'tenant'
ID = u'id'

#AUTHENTICATION
AUTHENTICATION_HEADERS = {'content-type': 'application/json', 'Accept': 'application/json'}
TENANT_NAME_VALUE = os.environ['OS_TENANT_NAME_VALUE']
USERNAME_VALUE = os.environ['OS_USERNAME_VALUE']
PASSWORD_VALUE = os.environ['OS_PASSWORD_VALUE']
KEYSTONE_URL = 'http://130.206.80.61:35357/v2.0/tokens'

#HEADERS
AUTH_TOKEN_HEADER = u'x-Auth-Token'

REPOSITORY_URL = 'http://repositories.testbed.fi-ware.eu:8889/upload'
RPM_LOCATION = str(sys.argv[1])

def get_token():
    '''Obtains a token from keystone to upload rpm afterwards'''
    print TENANT_NAME_VALUE
    print USERNAME_VALUE
    print PASSWORD_VALUE
	
    body = {AUTH: {TENANT_NAME: TENANT_NAME_VALUE, PASSWORD_CREDENTIALS: {USERNAME: USERNAME_VALUE,
                                                                           PASSWORD: PASSWORD_VALUE}}}
    body = json.dumps(body)
    print 'Body to request token: ' + body
    r = requests.request(method='post', url=KEYSTONE_URL, data=body, headers=AUTHENTICATION_HEADERS)
    response = r.json()
    token_id = response[ACCESS][TOKEN][ID]
    print 'Tokenid: ' + token_id
    return token_id

print "Paas Version "  + str(sys.argv[1])	
token_id = get_token()

def upload_rpm():
    '''Uploads the rpm to the repository'''
    print 'Uploading rpm to ' + REPOSITORY_URL
    print 'Rpm location: ' + RPM_LOCATION
    return_code = subprocess.call("mv " + RPM_LOCATION + " .", shell=True)
    RPM_NAME = ntpath.basename(RPM_LOCATION)
    files = {'file': (RPM_NAME, open(RPM_NAME, 'rb'), {'Expires': '0'})}
    AUTH_TOKEN_HEADERS = {AUTH_TOKEN_HEADER : token_id}
    print AUTH_TOKEN_HEADERS
    r = requests.request(method='post', url=REPOSITORY_URL, files=files, headers=AUTH_TOKEN_HEADERS)
    print r.status_code

upload_rpm()
