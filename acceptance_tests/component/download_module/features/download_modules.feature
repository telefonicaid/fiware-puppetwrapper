# -*- coding: utf-8 -*-

Feature: As a SDC user
         I want download puppet modules
         In order to install it in my virtual machines


Scenario Outline: Download a git repository

  Given a module "<module_name>"
  When I download the module from the "git" repository
    | URL                                                 |
    | https://github.com/puppetlabs/puppetlabs-mysql.git  |

  Then the module is downloaded


  Examples:
    | module_name   |
    | tonitest      |
    | postgres_git  |
    | a             |
    | @             |


Scenario Outline: Download a svn repository
  Given a module "<module_name>"
  When I download the module from the "svn" repository
    | URL                                                                                     |
    | https://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/puppetmysql/      |
  Then the module is downloaded


  Examples:
    | module_name     |
    | postgres_svn    |



Scenario Outline: Incorrect download
  Given a module "<module_name>"
  When I download the module from the "<repository>" repository
    | URL                                                                                     |
    | https://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/puppetmysql/      |
  Then I obtain an "<Error_code>"


  Examples:
    | module_name     | Error_code  | repository  |
    | postgres_error  | 404         | error       |
    | postgres_error  | 404         | Git         |
    | postgres_error  | 404         | Svn         |
    | postgres_error  | 404         | GIT         |
    | postgres_error  | 404         | SVN         |
    | postgres_error  | 404         | github      |
    | postgres_error  | 404         | subversion  |
    | postgres_error  | 404         | gitsvn      |


Scenario Outline: Incorrect download URL
  Given a module "<module_name>"
  When I download the module from the "<repository>" repository
    | URL     |
    | <url>   |
  Then I obtain an "<Error_code>"


  Examples:
    | module_name     | Error_code  | repository  | url                                                 |
    | postgres_error  | 400         | git         | http://github.com/puppetlabs/puppetlabs-mysql.git   |
    | postgres_error  | 400         | git         | git@github.com:puppetlabs/puppetlabs-mysql.git      |
    | postgres_error  | 400         | git         | https://github.com/puppetlabs/puppetlabs-mysql#     |
    | postgres_error  | 400         | svn         | https://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/not_existent     |
    | postgres_error  | 400         | svn         | https://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/puppetmysql#     |
    | postgres_error  | 400         | svn         | https://github.com     |
    | postgres_error  | 400         | svn         | http://forge.fi-ware.org/scmrepos/svn/testbed/trunk/cookbooks/Tester/puppetmysql/     |


Scenario Outline: Incorrect software name

  Given a module "<module_name>"
  When I download the module from the "git" repository
    | URL                                                 |
    | https://github.com/puppetlabs/puppetlabs-mysql.git  |

  Then I obtain an "<Error_code>"


  Examples:
    | module_name   | Error_code  |
    |               | 404         |

