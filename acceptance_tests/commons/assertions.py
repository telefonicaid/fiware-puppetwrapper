__author__ = 'arobres'
# -*- coding: utf-8 -*-


from nose.tools import assert_equals, assert_true, assert_in
from constants import INSTALL_GROUP_NAME, INSTALL_NODE_NAME, INSTALL_MANIFEST_GENERATED, OP_SOFTWARE_LIST

HTTP_CODE_NOT_OK = u'Invalid HTTP status code. Status Code obtained is: {}\n RESPONSE OBTAINED IS: {}'
INCORRECT_HTTP_STATUS_CODE = u'Invalid HTTP status code. Status Code obtained is: {}\n Status Code expected is: {}\n'
INCORRECT_PARAMETER_SIMPLE = u'Incorrect value for the parameter {}'
INCORRECT_PARAMETER = u'Incorrect value for the parameter {}\n. Expected value is: {} \n Obtained value is: {}'
DESCRIPTION = 'description'


def assert_install_response(response, node_name, group_name, software_to_generate_list=None):

    assert_response_ok(response)
    try:
        response = response.json()
    except:
        assert False
    assert_equals(response[INSTALL_NODE_NAME], node_name, INCORRECT_PARAMETER.format(INSTALL_NODE_NAME, node_name,
                                                                                     response[INSTALL_NODE_NAME]))
    assert_equals(response[INSTALL_GROUP_NAME], group_name, INCORRECT_PARAMETER.format(INSTALL_GROUP_NAME, group_name,
                                                                                       response[INSTALL_GROUP_NAME]))
    if software_to_generate_list is not None:
        for software in software_to_generate_list:
            assert_in(software, response[OP_SOFTWARE_LIST])


def assert_generate_response(response, node_name, group_name):

    assert_response_ok(response)
    try:
        response = response.json()
    except:
        assert False
    assert_equals(response[INSTALL_NODE_NAME], node_name, INCORRECT_PARAMETER.format(INSTALL_NODE_NAME, node_name,
                                                                                     response[INSTALL_NODE_NAME]))
    assert_equals(response[INSTALL_GROUP_NAME], group_name, INCORRECT_PARAMETER.format(INSTALL_GROUP_NAME, group_name,
                                                                                       response[INSTALL_GROUP_NAME]))
    assert_equals(response[INSTALL_MANIFEST_GENERATED], True)


def assert_error_code(response, error_code):

    obtained_status_code = str(response.status_code)
    error_code = str(error_code)
    assert_equals(obtained_status_code, error_code, INCORRECT_HTTP_STATUS_CODE.format(response.status_code, error_code))


def assert_response_ok(response):

    assert_true(response.ok, HTTP_CODE_NOT_OK.format(response.status_code, response.content))


def assert_message_description(response, message_description):

    assert_equals(response[DESCRIPTION], message_description)