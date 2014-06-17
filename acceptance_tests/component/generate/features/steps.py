__author__ = 'arobres'
# -*- coding: utf-8 -*-

from commons.rest_utils import RestUtils
from commons.constants import INSTALL, UNINSTALL, ACTION, VERSION, SOFTWARE_NAME
import commons.assertions as Assertions
import commons.fabric_utils as Fabutils

from lettuce import step, world, before


api_utils = RestUtils()

@before.each_scenario
def setup(scenario):

    world.software_to_generate = []



@step(u'Given the group "([^"]*)" and server "([^"]*)"')
def set_group_and_node_name(step, group, node_name):

    world.group = group
    world.node_name = node_name


@step(u'When I install the software "([^"]*)" with version "([^"]*)"')
def install_software(step, software_name, version):

    tmp_dict = {SOFTWARE_NAME: software_name, VERSION: version, ACTION: INSTALL}
    world.response = api_utils.install(group=world.group, node_name=world.node_name, software_name=software_name,
                                       version=version)

    world.software_to_generate.append(tmp_dict)


@step(u'Then the uninstall request is created in the system')
@step(u'Then the install request is created in the system')
def assert_install_uninstall(step):

    Assertions.assert_install_response(response=world.response, node_name=world.node_name, group_name=world.group)


@step(u'When I uninstall the software "([^"]*)" with version "([^"]*)"')
def uninstall_software(step, software_name, version):

    tmp_dict = {SOFTWARE_NAME: software_name, VERSION: version, ACTION: UNINSTALL}

    world.response = api_utils.uninstall(group=world.group, node_name=world.node_name,
                                         software_name=software_name, version=version)

    world.software_to_generate.append(tmp_dict)

@step(u'When I generate the manifest for the node "([^"]*)"')
def when_i_generate_the_manifest_for_the_node_group1(step, node_name):

    world.node_name = node_name
    world.response = api_utils.generate(node_name=world.node_name)


@step(u'Then the manifest in generated in the puppet master')
def then_the_manifest_in_generated_in_the_puppet_master(step):

    Assertions.assert_generate_response(response=world.response, node_name=world.node_name, group_name=world.group)

    for software in world.software_to_generate:

        Fabutils.execute_generate(node_name=world.node_name, software_name=software[SOFTWARE_NAME],
                                  version=software[VERSION], action=software[ACTION], group_name=world.group)

    world.software_to_generate = []


@step(u'Then I obtain an "([^"]*)"')
def then_i_obtain_an_group1(step, expected_status_code):

    world.software_to_generate = []
    Assertions.assert_error_code(response=world.response, error_code=expected_status_code)


@step(u'And a error message description "([^"]*)"')
def and_a_error_message_description_group1(step, message_error):

    response =  world.response.json()
    print response['description']
    Assertions.assert_message_description(response, message_error)