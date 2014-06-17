Feature: As a user
         I want to delete a module
         In order to not install this software in the nodes


Scenario Outline: Delete module downloaded from git

  Given a downloaded module from repository
    | URL                                                 | module_name   | repository  |
    | https://github.com/puppetlabs/puppetlabs-mysql.git  | <module_name> | git         |
  When I delete the module "<module_name>"
  Then the module is deleted from the system

  Examples:

  | module_name   |
  | tonitest      |
  | postgres_git  |
  | a             |
  | @             |


Scenario Outline: Delete module downloaded from svn

  Given a downloaded module from repository
    | URL                                                                                 | module_name   | repository  |
    | https://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/puppetmysql/  | <module_name> | svn         |
  When I delete the module "<module_name>"
  Then the module is deleted from the system

  Examples:

  | module_name   |
  | tonitest      |
  | postgres_svn  |
  | a             |
  | @             |

Scenario Outline: Delete non existent module

  Given a downloaded module from repository
    | URL                                                                                 | module_name   | repository  |
    | https://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/puppetmysql/  | sql_svn       | svn         |
    | https://github.com/puppetlabs/puppetlabs-mysql.git                                  | sql_git       | git         |
  When I delete the module "<module_name>"
  Then the module is not deleted from the system

  Examples:

  | module_name   |
  | testing       |
  | sql*          |
  | sql           |
  | SQL_SVN       |
  | Sql_svn       |
  | sql_Svn       |
  | sql-svn       |
  | sqlsvn        |

