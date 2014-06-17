__author__ = 'arobres'

from json import JSONEncoder

import requests

from configuration import PUPPET_WRAPPER_IP, PUPPET_WRAPPER_PORT, HEADERS

PUPPET_WRAPPER_SERVER = 'http://{}:{}/puppetwrapper'.format(PUPPET_WRAPPER_IP, PUPPET_WRAPPER_PORT)
INSTALL_PATTERN = '{url_root}/install/{group}/{nodeName}/{softwareName}/{version}'
UNINSTALL_PATTERN = '{url_root}/uninstall/{group}/{nodeName}/{softwareName}/{version}'
GENERATE_PATTERN = '{url_root}/generate/{nodeName}'
DELETE_NODE_PATTERN = '{url_root}/delete/node/{node_name}'
DELETE_MODULE_PATTERN = '{url_root}/delete/module/{software_name}'
DOWNLOAD_PATTERN = '{url_root}/download/{repository}/{software_name}'


class RestUtils(object):

    def __init__(self):
        """Initialization method
        """

        self.api_url = PUPPET_WRAPPER_SERVER
        self.encoder = JSONEncoder()

    def _call_api(self, pattern, method, body=None, headers=HEADERS, payload=None, **kwargs):

        """Launch HTTP request to Policy Manager API with given arguments
        :param pattern: string pattern of API url with keyword arguments (format string syntax)
        :param method: HTTP method to execute (string)
        :param body: JSON body content (dict)
        :param headers: HTTP header request (dict)
        :param payload: Query parameters for the URL
        :param **kwargs: URL parameters (without url_root) to fill the patters
        :returns: REST API response
        """

        kwargs['url_root'] = self.api_url

        url = pattern.format(**kwargs)

        try:
            r = requests.request(method=method, url=url, data=self.encoder.encode(body), headers=headers,
                                 params=payload)

        except Exception, e:
            print "Request {} to {} crashed: {}".format(method, url, str(e))
            return None

        return r

    def install(self, group=None, node_name=None, software_name=None, version=None, headers=HEADERS):

        return self._call_api(pattern=INSTALL_PATTERN, method='post', headers=headers, group=group, nodeName=node_name,
                              softwareName=software_name, version=version)

    def uninstall(self, group=None, node_name=None, software_name=None, version=None, headers=HEADERS):

        return self._call_api(pattern=UNINSTALL_PATTERN, method='post', headers=headers, group=group,
                              nodeName=node_name, softwareName=software_name, version=version)

    def generate(self, node_name=None, headers=HEADERS):

        return self._call_api(pattern=GENERATE_PATTERN, method='post', headers=headers, nodeName=node_name)

    def delete_node(self, node_name, headers=HEADERS):

        return self._call_api(pattern=DELETE_NODE_PATTERN, method='delete', headers=headers, node_name=node_name)

    def delete_module(self, software_name, headers=HEADERS):

        return self._call_api(pattern=DELETE_MODULE_PATTERN, method='delete', headers=headers,
                              software_name=software_name)

    def download_module(self, software_name=None, repository=None, url=None, headers=HEADERS):

        api_body = {}
        if url is not None:

            api_body['url'] = url

        return self._call_api(pattern=DOWNLOAD_PATTERN, method='post', headers=headers, software_name=software_name,
                              repository=repository, body=api_body)
