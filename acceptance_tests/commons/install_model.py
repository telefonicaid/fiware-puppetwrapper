__author__ = 'jfernandez'

from constants import GROUP, VERSION, SOFTWARE_NAME, OP_SOFTWARE_NAME, INSTALL_ATTRIBUTES, ACTION, INSTALL, UNINSTALL


# Model: {"attributes": [], "version": "", "group": "", "softwareName": ""}


def install_simple_model(version, group, software_name):
    return {VERSION: version, GROUP: group, SOFTWARE_NAME: software_name}


def install_attributes_model(version, group, software_name, attribute_list):
    return {VERSION: version, GROUP: group, SOFTWARE_NAME: software_name, INSTALL_ATTRIBUTES: attribute_list}


def _software_to_manage_response_model(software_name, version, action, attributes):
    if attributes is not None:
        return {OP_SOFTWARE_NAME: software_name, VERSION: version, ACTION: action, INSTALL_ATTRIBUTES: attributes}
    else:
        return {OP_SOFTWARE_NAME: software_name, VERSION: version, ACTION: action}


def software_to_install_response_model(software_name, version, attributes=None):
    return _software_to_manage_response_model(software_name, version, INSTALL, attributes)


def software_to_uninstall_response_model(software_name, version, attributes=None):
    return _software_to_manage_response_model(software_name, version, UNINSTALL, attributes)
