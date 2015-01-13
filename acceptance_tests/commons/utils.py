__author__ = 'jfernandez'

from constants import ACCEPT_HEADER_JSON, CONTENT_TYPE_XML
import xmltodict
import dicttoxml
import json


def _xml_to_dict(xml_to_convert, attr_prefix=''):
    """
    Function to convert XML response to Python dict.
    :param xml_to_convert: XML to be converted
    :param attr_prefix: If response has attributes, this will be the prefix used after parsing.
    By default: Without prefix.
    :return: Python dict with XML data parsed
    """
    return xmltodict.parse(xml_to_convert, attr_prefix=attr_prefix)


def _dic_to_xml(python_dict, root_element_name):
    """
    Function to convert a Python dict to XML without attributes.
    :param python_dict: Dict to be converted
    :param root_element_name Root node name to wraps all the elements under it
    :return: XML with all python dict data into a root element.
    """
    return dicttoxml.dicttoxml(python_dict, custom_root=root_element_name, attr_type=False)


def response_body_to_dict(http_response, accept_content_type, xml_root_element_name=None, is_list=False):
    """
    Function to convert a XML o JSON response in a Python dict
    :param http_response: 'Requests (lib)' response
    :param accept_content_type: Accept header value
    :param xml_root_element_name: For XML requests. XML root element in response.
    :param is_list: For XML requests. If response is a list, a True value will delete list node name
    :return: Python dict with response.
    """
    if ACCEPT_HEADER_JSON in accept_content_type:
        try:
            return http_response.json()
        except Exception, e:
            print str(e)
    else:
        assert xml_root_element_name is None,\
            "xml_root_element_name is a mandatory param when body is in XML"
        response_body = _xml_to_dict(http_response.content)[xml_root_element_name]

        if is_list and response_body is not None:
            response_body = response_body.popitem()[1]

        return response_body


def body_model_to_body_request(body_model, content_type, xml_root_element_name=None):
    """
    Function to convert a python model (body request) to JSON or XML
    :param body_model: Python type with data for request
    :param content_type: Target type to convert the model (application/json | application/xml)
    :param xml_root_element_name: If target request is XML, root element will be created with that name.
    :return: Body model converted to XML or JSON (without root element)
    """
    if CONTENT_TYPE_XML in content_type:
        assert xml_root_element_name is not None,\
            "xml_root_element_name is a mandatory param when body is in XML"
        return _dic_to_xml(body_model, xml_root_element_name)
    else:
        return json.dumps(body_model)
