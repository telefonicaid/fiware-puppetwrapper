__author__ = 'arobres'
# -*- coding: utf-8 -*-

from commons.rest_utils import RestUtils
from commons.constants import ACTION, VERSION, OP_SOFTWARE_NAME, UNINSTALL, AUTH_TOKEN_HEADER, TENANT_ID_HEADER, \
    CONTENT_TYPE, ACCEPT_HEADER
import commons.assertions as Assertions
import commons.fabric_utils as Fabutils
from commons.install_model import install_simple_model, software_to_install_response_model, software_to_uninstall_response_model
from commons.utils import body_model_to_body_request
from lettuce import step, world


api_utils = RestUtils()


@step(u'the group "([^"]*)" and server "([^"]*)"')
def set_group_and_node_name(step, group, node_name):

    world.group = group
    world.node_name = node_name


@step(u'the authentication token "([^"]*)"')
def the_authentication_token_group1(step, token):
    world.headers[AUTH_TOKEN_HEADER] = token


@step(u'the authentication tenant-id "([^"]*)"')
def the_authentication_tenant_id_group1(step, tenant_id):
    world.headers[TENANT_ID_HEADER] = tenant_id


@step(u'content-type header value "([^"]*)"')
def accept_header_value_group1(step, content_type_header):
    world.headers[CONTENT_TYPE] = content_type_header


@step(u'accept header value "([^"]*)"')
def accept_header_value_group1(step, accept_header):
    world.headers[ACCEPT_HEADER] = accept_header


@step(u'I install the software "([^"]*)" with version "([^"]*)"$')
def install_software(step, software_name, version):

    model_install_request = install_simple_model(version, world.group, software_name)
    body_request = body_model_to_body_request(model_install_request, world.content_type)
#    tmp_dict = {SOFTWARE_NAME: software_name, VERSION: version, GROUP: world.group}
    world.response = api_utils.install(world.node_name, body_request, headers=world.headers)

    # Save software installation structure and node name to be checked
    world.software_to_generate.append(software_to_install_response_model(software_name, version))
    if world.node_name not in world.configured_node_list:
        world.configured_node_list.append(world.node_name)


@step(u'I install the software "([^"]*)" with version "([^"]*)" using invalid HTTP "([^"]*)" method$')
def i_install_the_software_group1_with_version_group2_using_invalid_http(step, software_name, version, http_method):
    body = install_simple_model(version, world.group, software_name)
    world.response = api_utils.install(world.node_name, body, world.headers, method=http_method)

    # Save node name in configured_node_list to be deleted in tear_down
    world.configured_node_list.append(world.node_name)


@step(u'the uninstall request is created in the system')
@step(u'the install request is created in the system')
def assert_install_uninstall(step):

    Assertions.assert_install_response(response=world.response, node_name=world.node_name, group_name=world.group,
                                       software_to_generate_list=world.software_to_generate)


@step(u'I uninstall the software "([^"]*)" with version "([^"]*)"$')
def uninstall_software(step, software_name, version):

    model_install_request = install_simple_model(version, world.group, software_name)
    body_request = body_model_to_body_request(model_install_request, world.content_type)
    world.response = api_utils.uninstall(world.node_name, body_request, headers=world.headers)

    if len(world.software_to_generate) == 0:
        # Save software installation structure and node name to be checked
        world.software_to_generate.append(software_to_uninstall_response_model(software_name, version))
    else:
        # Update software operation for validations
        for software in world.software_to_generate:
            if software[OP_SOFTWARE_NAME] == software_name and software[VERSION] == version:
                software[ACTION] = UNINSTALL
                #software[INSTALL_ATTRIBUTES] = None #TODO: CLAUDIA-4366. To comment out when fixed

    if world.node_name not in world.configured_node_list:
        world.configured_node_list.append(world.node_name)


@step(u'I uninstall the software "([^"]*)" with version "([^"]*)" using invalid HTTP "([^"]*)" method$')
def i_uninstall_the_software_group1_with_version_group2_using_invalid_http(step, software_name, version, http_method):
    body = install_simple_model(version, world.group, software_name)
    world.response = api_utils.uninstall(world.node_name, body, world.headers, method=http_method)

    # Save node name in configured_node_list to be deleted in tear_down
    world.configured_node_list.append(world.node_name)


@step(u'the software "([^"]*)" with version "([^"]*)" is installed')
def the_software_group1_with_version_group2_is_installed(step, software_name, version):
    install_software(step, software_name, version)
    assert_install_uninstall(step)


@step(u'the software "([^"]*)" with version "([^"]*)" is uninstalled')
def the_software_group1_with_version_group2_is_uninstalled(step, software_name, version):
    uninstall_software(step, software_name, version)
    assert_install_uninstall(step)


@step(u'I generate the manifest for the node "([^"]*)"$')
def when_i_generate_the_manifest_for_the_node_group1(step, node_name):

    world.node_name = node_name
    world.response = api_utils.generate(node_name=world.node_name, headers=world.headers)


@step(u'I generate the manifest for the node "([^"]*)" using invalid HTTP "([^"]*)" method$')
def when_i_generate_the_manifest_for_the_node_group1_using_invalid_http(step, node_name, http_method):
    world.node_name = node_name
    world.response = api_utils.generate(node_name=world.node_name, headers=world.headers, method=http_method)


@step(u'the manifest is generated in the puppet master')
def then_the_manifest_in_generated_in_the_puppet_master(step):

    Assertions.assert_generate_response(response=world.response, node_name=world.node_name, group_name=world.group)

    for software in world.software_to_generate:
        Fabutils.execute_generate(node_name=world.node_name, software_name=software[OP_SOFTWARE_NAME],
                                  version=software[VERSION], action=software[ACTION], group_name=world.group)


@step(u'I obtain an "([^"]*)"')
def then_i_obtain_an_group1(step, expected_status_code):

    Assertions.assert_error_code(response=world.response, error_code=expected_status_code)


@step(u'a error message description "([^"]*)"')
def and_a_error_message_description_group1(step, message_error):

    response = world.response.json()
    print response['description']
    Assertions.assert_message_description(response, message_error)
