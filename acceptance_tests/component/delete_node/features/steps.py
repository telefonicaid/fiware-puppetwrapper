__author__ = 'arobres'

# -*- coding: utf-8 -*-

from commons.rest_utils import RestUtils
from commons.constants import INSTALL, UNINSTALL, ACTION, SOFTWARE_NAME, VERSION
from nose.tools import assert_equals
import commons.assertions as Assertions
import commons.fabric_utils as Fabutils


from lettuce import step, world


api_utils = RestUtils()


@step(u'Given a generated node "([^"]*)" in the tenant "([^"]*)"')
def given_a_generated_node_group1_in_the_tenant_group2(step, node_name, group_name):

    world.node_name = node_name
    world.group_name = group_name

    for examples in step.hashes:
        print examples[ACTION]
        if examples[ACTION] == INSTALL:
            res = api_utils.install(group=group_name, node_name=node_name, software_name=examples[SOFTWARE_NAME],
                                    version=examples[VERSION])

            Assertions.assert_response_ok(response=res)
        elif examples[ACTION] == UNINSTALL:
            res = api_utils.uninstall(group=group_name, node_name=node_name, software_name=examples[SOFTWARE_NAME],
                                      version=examples[VERSION])

            Assertions.assert_install_response(response=res, node_name=node_name, group_name=group_name)
        else:
            assert False, 'Not valid action'

    res = api_utils.generate(node_name=node_name)

    Assertions.assert_response_ok(response=res)


@step(u'When I delete the node "([^"]*)"')
def when_i_delete_the_node_group1(step, node_name):

    world.response = api_utils.delete_node(node_name=node_name)


@step(u'Then the node file is deleted')
def then_the_node_is_deleted(step):

    Assertions.assert_response_ok(world.response)

    assert_equals(Fabutils.execute_delete_node(group=world.group_name, node_name=world.node_name), False)


@step(u'And the import is removed from the site file')
def and_the_import_is_removed_from_the_site_file(step):

    assert_equals(Fabutils.execute_import_deleted(group=world.group_name), False)


@step(u'The node is not deleted from the system')
def the_node_is_not_deleted_from_the_system(step):

    Assertions.assert_response_ok(world.response)

    assert_equals(Fabutils.execute_delete_node(group=world.group_name, node_name=world.node_name), True)
    assert_equals(Fabutils.execute_import_deleted(group=world.group_name), True)
