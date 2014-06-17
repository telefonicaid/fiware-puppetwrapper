__author__ = 'arobres'

# -*- coding: utf-8 -*-

from commons.rest_utils import RestUtils
from nose.tools import assert_true, assert_false
import commons.assertions as Assertions
import commons.fabric_utils as Fabutils
from commons.constants import URL, MODULE_NAME, REPOSITORY


from lettuce import step, world, before

api_utils = RestUtils()

@before.each_scenario
def setup(scenario):

    world.software_downloaded = []


@step(u'Given a downloaded module from repository')
def given_a_downloaded_module_from_repository(step):

    for examples in step.hashes:
        url = examples[URL]
        module_name = examples[MODULE_NAME]
        repository = examples[REPOSITORY]
        response = api_utils.download_module(software_name=module_name, repository=repository, url=url)
        Assertions.assert_response_ok(response)
        assert_true(Fabutils.execute_assert_download(module_name))
        world.software_downloaded.append(module_name)


@step(u'When I delete the module "([^"]*)"')
def when_i_delete_the_module_group1(step, module_name):

    world.module_name = module_name
    world.response = api_utils.delete_module(software_name=module_name)


@step(u'Then the module is deleted from the system')
def then_the_module_is_deleted_from_the_system(step):
    Assertions.assert_response_ok(world.response)
    assert_false(Fabutils.execute_assert_download(world.module_name))


@step(u'Then the module is not deleted from the system')
def then_the_module_is_not_deleted_from_the_system(step):

    Assertions.assert_response_ok(world.response)
    assert_false(Fabutils.execute_assert_download(world.module_name))
    for module in world.software_downloaded:
        assert_true(Fabutils.execute_assert_download(module))

