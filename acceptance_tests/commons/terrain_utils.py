__author__ = 'jfernandez'

from lettuce import world
from commons.authentication_utils import get_auth_data_from_keystone
from constants import AUTH_TOKEN_HEADER, TENANT_ID_HEADER, CONTENT_TYPE, CONTENT_TYPE_JSON, ACCEPT_HEADER, ACCEPT_HEADER_JSON
from commons.rest_utils import RestUtils

rest_utils = RestUtils()


def _get_default_headers(token_id, tenant_id):
    """ Helper: Generate default auth header to be used by TC requests """
    headers = dict()
    headers.update({AUTH_TOKEN_HEADER: token_id})
    headers.update({TENANT_ID_HEADER: tenant_id})
    headers.update({CONTENT_TYPE: CONTENT_TYPE_JSON})
    headers.update({ACCEPT_HEADER: ACCEPT_HEADER_JSON})

    # Save default headers to be used by TCs.
    world.content_type = CONTENT_TYPE_JSON
    world.accept = ACCEPT_HEADER_JSON

    return headers


def setup_feature():
    """ Configure global features: Get token and tenant from Keystone and configure default auth headers """
    world.token_id, world.tenant_id = get_auth_data_from_keystone()
    #world.headers = _get_default_headers(world.token_id, world.tenant_id)


def setup_scenario():
    """ Configure TC. Init global vars to be used by Scenarios """
    world.headers = _get_default_headers(world.token_id, world.tenant_id)
    world.configured_node_list = list()
    world.software_to_generate = list()


def setup_outline():
    """ Configure Outline. """
    world.headers = _get_default_headers(world.token_id, world.tenant_id)


def tear_down():
    """ Teardown actions: Clean test data from environment """
    for node in world.configured_node_list:
        rest_utils.delete_node(node, world.headers)
    world.configured_node_list = []
