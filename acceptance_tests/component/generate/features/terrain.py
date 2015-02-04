__author__ = 'jfernandez'

from lettuce import world, before, after
from commons import terrain_utils


@before.each_feature
def before_each_feature(feature):
    """ Lettuce hock: To be executed before each feature"""
    terrain_utils.setup_feature()


@before.each_scenario
def before_each_scenario(scenario):
    """ Lettuce hock: To be execute before each scenario. Inits world vars to be used by each scenario """
    terrain_utils.setup_scenario()


@before.outline
def before_outline(param1, param2, param3, param4):
    """ Lettuce hook: Will be executed before each Scenario Outline. Same behaviour as 'before_each_scenario'"""
    world.software_to_generate = list()
    terrain_utils.setup_outline()


@after.each_scenario
def after_each_scenario(scenario):
    """ Lettuce hock: To be executed after each scenario. Cleans vars and environment. """
    terrain_utils.tear_down()


@after.all
def after_all(scenario):
    """ Lettuce hock: To be executed after all scenarios. Cleans vars and environment. """
    terrain_utils.tear_down()
