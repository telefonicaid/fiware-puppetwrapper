__author__ = 'arobres, jfernandez'

from json import JSONEncoder

import requests

from configuration import PUPPET_MASTER_PROTOCOL, PUPPET_WRAPPER_IP, PUPPET_WRAPPER_PORT, CONFIG_KEYSTONE_URL

PUPPET_WRAPPER_SERVER = '{}://{}:{}/puppetwrapper'.format(PUPPET_MASTER_PROTOCOL, PUPPET_WRAPPER_IP,
                                                          PUPPET_WRAPPER_PORT)
INSTALL_PATTERN = '{url_root}/v2/node/{nodeName}/install'
UNINSTALL_PATTERN = '{url_root}/v2/node/{nodeName}/uninstall'
GENERATE_PATTERN = '{url_root}/v2/node/{nodeName}/generate'
DELETE_NODE_PATTERN = '{url_root}/v2/node/{node_name}'
DELETE_MODULE_PATTERN = '{url_root}/delete/module/{software_name}'
DOWNLOAD_PATTERN = '{url_root}/download/{repository}/{software_name}'


class RestUtils(object):

    def __init__(self):
        """ Initialization method """

        self.api_url = PUPPET_WRAPPER_SERVER
        self.encoder = JSONEncoder()

    def _call_api(self, pattern, method, body=None, headers=None, payload=None, **kwargs):
        """
        Launch HTTP request to API with given arguments
        :param pattern: string pattern of API url with keyword arguments (format string syntax)
        :param method: HTTP method to execute (string)
        :param body: JSON/XML body content
        :param headers: HTTP header request (dict)
        :param payload: Query parameters for the URL
        :param **kwargs: URL parameters (without url_root) to fill the patters
        :returns: REST API response
        """
        kwargs['url_root'] = self.api_url

        url = pattern.format(**kwargs)

        #print ""
        #print "### REQUEST ###"
        #print 'METHOD: {}\nURL: {} \nHEADERS: {} \nBODY: {}'.format(method, url, headers, self.encoder.encode(body))

        try:
            r = requests.request(method=method, url=url, data=body, headers=headers, params=payload, verify=False)
        except Exception, e:
            print "Request {} to {} crashed: {}".format(method, url, str(e))
            return None

        #print "### RESPONSE ###"
        #print "HTTP RESPONSE CODE:", r.status_code
        #print 'HEADERS: {} \nBODY: {}'.format(r.headers, r.content)
        #print ""

        return r

    @staticmethod
    def get_keystone_token(body, headers=None):
        return requests.request(method='post', url=CONFIG_KEYSTONE_URL, data=body, headers=headers, verify=False)

    def install(self, node_name, body, headers=None, method='post'):
        """
        POST /puppetwrapper/v2/node/{nodeName}/install
        {
            "attributes": [{
                "value": "valor",
                "key": "clave",
                "id": 23119,
                "description": null
            }],
            "version": "0.1",
            "group": "alberts",
            "softwareName": "testPuppet"
        }
        """

        return self._call_api(pattern=INSTALL_PATTERN, method=method, body=body, headers=headers, nodeName=node_name)

    def uninstall(self, node_name, body, headers=None, method='post'):
        """
        POST /puppetwrapper/v2/node/{nodeName}/uninstall
        {
            "attributes": [{
                "value": "valor",
                "key": "clave",
                "id": 23119,
                "description": null
            }],
            "version": "0.1",
            "group": "alberts",
            "softwareName": "testPuppet"
        }
        """
        return self._call_api(pattern=UNINSTALL_PATTERN, method=method, body=body, headers=headers, nodeName=node_name)

    def generate(self, node_name, headers=None, method='get'):
        """
        GET /puppetwrapper/v2/node/{nodeName}/generate
        """
        return self._call_api(pattern=GENERATE_PATTERN, method=method, headers=headers, nodeName=node_name)

    def delete_node(self, node_name, headers=None):
        """
        DELETE /puppetwrapper/v2/node/{nodeName}
        """
        return self._call_api(pattern=DELETE_NODE_PATTERN, method='delete', headers=headers, node_name=node_name)

    def delete_module(self, software_name, headers=None):

        return self._call_api(pattern=DELETE_MODULE_PATTERN, method='delete', headers=headers,
                              software_name=software_name)

    def download_module(self, software_name=None, repository=None, url=None, headers=None):

        api_body = {}
        if url is not None:

            api_body['url'] = url

        return self._call_api(pattern=DOWNLOAD_PATTERN, method='post', headers=headers, software_name=software_name,
                              repository=repository, body=api_body)
