Feature: As a SDC user
         I want to create automatically manifests
         In order to install and uninstall from Puppet


Scenario Outline: : Create a new install node

  Given the group "<group>" and server "<node_name>"
  When I install the software "<software_name>" with version "<version>"
  Then the install request is created in the system

  Examples:

  | group     | node_name     | software_name | version |
  | qaserver  | QASERVER      | mysql         | 1.0     |
  | qaserver  | testingserver | mysql         | 0.0.1   |
  | qaserver  | testingserver | mysql         | 1       |


Scenario Outline: : Create a new uninstall node

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  When I uninstall the software "<software_name>" with version "<version>"
  Then the uninstall request is created in the system

  Examples:

  | group     | node_name     | software_name | version |
  | qaserver  | QASERVER      | mysql         | 1.0     |
  | qaserver  | testingserver | mysql         | 0.0.1   |
  | qaserver  | testingserver | mysql         | 1       |


Scenario Outline: Generate new install manifest with only one product

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  When I generate the manifest for the node "<node_name>"
  Then the manifest is generated in the puppet master

  Examples:

  | group     | node_name     | software_name | version |
  | qaserver  | QASERVER      | mysql         | 1.0     |
  | qaserver  | testingserver | mysql         | 0.0.1   |
  | qaserver  | testingserver | mysql         | 1       |


Scenario Outline: Generate new uninstall manifest with only one product

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  And the software "<software_name>" with version "<version>" is uninstalled
  When I generate the manifest for the node "<node_name>"
  Then the manifest is generated in the puppet master

  Examples:

  | group     | node_name     | software_name | version |
  | qaserver  | QASERVER      | mysql         | 1.0     |
  | qaserver  | testingserver | mysql         | 0.0.1   |
  | qaserver  | testingserver | mysql         | 1       |


Scenario Outline: Generate new install manifest with several products

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  And the software "<another_software_name>" with version "<another_version>" is installed
  When I generate the manifest for the node "<node_name>"
  Then the manifest is generated in the puppet master

  Examples:

  | group     | node_name     | software_name | version | another_software_name | another_version |
  | qaserver  | QASERVER      | mysql         | 1.0     | mongodb               | 3               |
  | qaserver  | testingserver | mysql         | 0.0.1   | mongodb               | 3.0             |
  | qaserver  | testingserver | mysql         | 1       | mongodb               | 3.3.a           |


Scenario Outline: Generate new uninstall manifest with several products

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  And the software "<another_software_name>" with version "<another_version>" is installed
  And the software "<software_name>" with version "<version>" is uninstalled
  And the software "<another_software_name>" with version "<another_version>" is uninstalled
  When I generate the manifest for the node "<node_name>"
  Then the manifest is generated in the puppet master

  Examples:

  | group     | node_name     | software_name | version | another_software_name | another_version |
  | qaserver  | QASERVER      | mysql         | 1.0     | mongodb               | 3               |
  | qaserver  | testingserver | mysql         | 0.0.1   | mongodb               | 3.0             |
  | qaserver  | testingserver | mysql         | 1       | mongodb               | 3.3.a           |


Scenario Outline: Generate new manifest with several products to install and uninstall

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  And the software "<another_software_name>" with version "<another_version>" is installed
  And the software "<another_software_name>" with version "<another_version>" is uninstalled
  When I generate the manifest for the node "<node_name>"
  Then the manifest is generated in the puppet master

  Examples:

  | group     | node_name     | software_name | version | another_software_name | another_version |
  | qaserver  | QASERVER      | mysql         | 1.0     | mongodb               | 3               |
  | qaserver  | testingserver | mysql         | 0.0.1   | mongodb               | 3.0             |
  | qaserver  | testingserver | mysql         | 1       | mongodb               | 3.3.a           |


Scenario Outline: Generate new manifest from nonexistent node

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  When I generate the manifest for the node "<another_node_name>"
  Then I obtain an "<Error_code>"
  And a error message description "<error_message>"

  Examples:

  | group     | node_name     | software_name | version | another_node_name | Error_code  | error_message                         |
  | qaserver  | QASERVER      | mysql         | 1.0     | qaserver_         | 404         | The node qaserver_ could not be found |
  | qaserver  | testingserver | mysql         | 0.0.1   | lalala            | 404         | The node lalala could not be found   |
  | qaserver  | testingserver | mysql         | 1       | hello             | 404         | The node hello could not be found     |


Scenario Outline: Error creating a uninstall manifest from non existent software

  Given the group "<group>" and server "<node_name>"
  And the software "<software_name>" with version "<version>" is installed
  When I uninstall the software "<another_node_name>" with version "<version>"
  Then I obtain an "<Error_code>"
  And a error message description "<error_message>"


  Examples:

  | group     | node_name     | software_name | another_node_name | version | Error_code  | error_message                                 |
  | qaserver  | QASERVER      |  mysql        | not_installed     | 1.0     | 404         | The software not_installed could not be found |
  | qaserver  | testingserver |  mysql        | never_install     | 0.0.1   | 404         | The software never_install could not be found |
  | qaserver  | testingserver |  mysql        | not_found         | 1       | 404         | The software not_found could not be found     |
