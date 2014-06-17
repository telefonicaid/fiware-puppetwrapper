__author__ = 'arobres'
# -*- coding: utf-8 -*-

from commons.rest_utils import RestUtils
from commons.constants import URL
import commons.assertions as Assertions
import commons.fabric_utils as Fabutils



from lettuce import step, world, before
from nose.tools import assert_true

api_utils = RestUtils()


@step(u'Given a module "([^"]*)"')
def given_a_module_group1(step, software_name):

    world.software_name = software_name


@step(u'When I download the module from the "([^"]*)" repository')
def when_i_download_the_module_from_the_group1_repository(step, repository):

    url = step.hashes[0][URL]
    world.response = api_utils.download_module(software_name=world.software_name, repository=repository, url=url)


@step(u'Then the module is downloaded')
def then_the_module_is_downloaded(step):

    Assertions.assert_response_ok(world.response)
    assert_true(Fabutils.execute_assert_download(world.software_name))


@step(u'Then I obtain an "([^"]*)"')
def then_i_obtain_an_group1(step, expected_status_code):

    world.software_to_generate = []
    Assertions.assert_error_code(response=world.response, error_code=expected_status_code)

