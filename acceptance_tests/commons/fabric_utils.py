from fabric.api import run, env, settings, task
from fabric.tasks import execute as fabric_execute
from fabric.contrib import files
from nose.tools import assert_true
from configuration import PUPPET_MASTER_USERNAME, PUPPET_MASTER_PWD, PUPPET_WRAPPER_IP
from constants import PUPPET_MASTER_SPECIFIC_MANIFEST, PUPPET_MASTER_SITE, SITE_PP_TEXT, PUPPET_MASTER_SPECIFIC_MODULE,\
    FABRIC_RESULT_EXECUTE

env.user = PUPPET_MASTER_USERNAME
env.password = PUPPET_MASTER_PWD
env.host_string = PUPPET_WRAPPER_IP


def calculate_path(group, node_name):

    return PUPPET_MASTER_SPECIFIC_MANIFEST.format(group, node_name)


def assert_module_exist(module_name):

    file_path = PUPPET_MASTER_SPECIFIC_MODULE.format(module_name)
    return files.exists(file_path)


def assert_specific_manifest_file_exist(group=None, node_name=None):

    file_path = calculate_path(group=group, node_name=node_name)
    exist = files.exists(file_path)
    assert_true(exist)


def assert_site_file_exist():

    assert_site_file_exist()
    exist = files.exists(PUPPET_MASTER_SITE)
    assert_true(exist)


def assert_site_file_content(group_name):

    content_exist = files.contains(PUPPET_MASTER_SITE, SITE_PP_TEXT.format(group_name))
    assert_true(content_exist)


def assert_manifest_file_content(node_name, software_name, version, action, group):

    file_path = calculate_path(group=group, node_name=node_name)
    assert_specific_manifest_file_exist(group=group, node_name=node_name)

    node_string = "node '{}'".format(node_name)
    class_string = "'{}::{}':".format(software_name, action.lower())
    version_string = "'{}'".format(version)

    assert_true(files.contains(file_path, node_string))
    assert_true(files.contains(file_path, class_string))
    assert_true(files.contains(file_path, version_string))


def assert_node_file_not_exist(group, node_name):

    file_path = calculate_path(group=group, node_name=node_name)
    exist = files.exists(file_path)
    return exist


def assert_site_file_not_content_group(group):

    content_exist = files.contains(PUPPET_MASTER_SITE, SITE_PP_TEXT.format(group))
    return content_exist


def assert_generate(node_name, software_name, version, action, group_name):

    assert_site_file_content(group_name=group_name)
    assert_manifest_file_content(node_name=node_name, software_name=software_name, action=action,
                                 version=version, group=group_name)


def execute_delete_node(group, node_name):

    success = fabric_execute(assert_node_file_not_exist, group=group, node_name=node_name)
    return success[FABRIC_RESULT_EXECUTE]


def execute_import_deleted(group):

    success = fabric_execute(assert_site_file_not_content_group, group=group)
    return success[FABRIC_RESULT_EXECUTE]


def execute_generate(node_name, software_name, version, action, group_name):

    fabric_execute(assert_generate, node_name=node_name, software_name=software_name, version=version, action=action,
                   group_name=group_name)


def execute_assert_download(module_name):

    success = fabric_execute(assert_module_exist, module_name=module_name)
    return success[FABRIC_RESULT_EXECUTE]
