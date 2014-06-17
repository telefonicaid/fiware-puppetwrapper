Feature: As a user
         I want to delete a node
         In order to not install more software in this node


Scenario Outline: Delete a node with only one product

  Given a generated node "<node_name>" in the tenant "<group_name>" with the following software
    | software_name | version | action    |
    | mysql         | 1       | <action>  |
  When I delete the node "<node_name>"
  Then the node file is deleted
  And the import is removed from the site file

  Examples:

  | group_name  | node_name | action    |
  | delete      | node1     | install   |
  | delete      | node2     | uninstall |


Scenario Outline: Delete a node with several products with the same action

  Given a generated node "<node_name>" in the tenant "<group_name>" with the following software
    | software_name | version | action    |
    | mysql         | 1       | <action>  |
    | mongo         | 2       | <action>  |

  When I delete the node "<node_name>"
  Then the node file is deleted
  And the import is removed from the site file

  Examples:

  | group_name  | node_name | action    |
  | delete      | node1     | install   |


Scenario Outline: Delete a node with several products with the same action and different versions

  Given a generated node "<node_name>" in the tenant "<group_name>" with the following software
    | software_name | version | action    |
    | mysql         | 1       | <action>  |
    | mysql         | 2       | <action>  |
    | mysql         | 3.0     | <action>  |

  When I delete the node "<node_name>"
  Then the node file is deleted
  And the import is removed from the site file

  Examples:

  | group_name  | node_name | action    |
  | delete      | node1     | install   |


Scenario Outline: Delete a node with several products with different actions

  Given a generated node "<node_name>" in the tenant "<group_name>" with the following software
    | software_name | version | action    |
    | mysql         | 1       | install   |
    | mysql         | 2       | uninstall |
    | mysql         | 3.0     | <action>  |

  When I delete the node "<node_name>"
  Then the node file is deleted
  And the import is removed from the site file

  Examples:

  | group_name  | node_name | action    |
  | delete      | node1     | install   |
  | delete      | node2     | uninstall |


Scenario Outline: Delete a non existent node

  Given a generated node "<node_name>" in the tenant "<group_name>" with the following software
    | software_name | version | action    |
    | mysql         | 1       | install   |

  When I delete the node "<another_node_name>"
  The node is not deleted from the system

  Examples:

  | group_name      | node_name | another_node_name |
  | incorrect_delete| node1     | not_existant_node |
  | incorrect_delete| node2     | NODE2             |

