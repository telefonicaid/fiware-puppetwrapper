Feature: As a SDC user
         I want to check some security and auth restrictions when installing software
         so that I can work with manifest safely


  Scenario Outline: Unsupported HTTP methods to create configurations for software installation
    Given the group "qagroup" and server "qanode"
    When  I install the software "qa_product_puppet" with version "1.0.0" using invalid HTTP "<http_method>" method
    Then  I obtain an "405"

    Examples:
    | http_method |
    | get         |
    | put         |
    | delete      |

  @skip @CLAUDIA-4460
  Scenario Outline: Create a new configuration to install software using invalid content_type

    Given the group "qagroup" and server "qanode"
    And   content-type header value "<content_type>"
    When  I install the software "qa_product_puppet" with version "1.0.0"
    Then  I obtain an "415"

    Examples:
    | content_type     |
    | application/xml   |
    | application/lalal |
    | application/json1 |


  Scenario Outline: Create a new configuration to install software using invalid accept header

    Given the group "qagroup" and server "qanode"
    And   accept header value "<accept_header>"
    When  I install the software "qa_product_puppet" with version "1.0.0"
    Then  I obtain an "406"

    Examples:
    | accept_header     |
    | application/xml   |
    | application/lalal |
    | application/json1 |


  @auth
  Scenario Outline: Create a configuration to install software with incorrect authentication: token
    Given the group "qagroup" and server "qanode"
    And   the authentication token "<token>"
    When  I install the software "qa_product_puppet" with version "1.0.0"
    Then  I obtain an "401"

    Examples:

    | token                            |
    | hello world                      |
    | 891855f21b2f1567afb966d3ceee1295 |
    |                                  |


  @auth
  Scenario Outline: Create a configuration to install software with incorrect authentication: token
    Given the group "qagroup" and server "qanode"
    And   the authentication tenant-id "<tenant_id>"
    When  I install the software "qa_product_puppet" with version "1.0.0"
    Then  I obtain an "401"

    Examples:

    | tenant_id                        |
    | hello world                      |
    | 00001231234112                   |
    |                                  |
