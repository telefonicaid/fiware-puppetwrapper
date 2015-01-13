Feature: As a SDC user
         I want to check some security and auth restrictions when I generate manifests
         so that I can work with manifest safely


  Scenario Outline: Unsupported HTTP methods when generating manifests
    Given the group "qagroup" and server "qanode"
    And   the software "<software_name>" with version "<version>" is installed
    When  I generate the manifest for the node "qanode" using invalid HTTP "<http_method>" method
    Then  I obtain an "405"

    Examples:
    | http_method |
    | post        |
    | put         |
    | delete      |


  Scenario Outline: Generate manifest using invalid accept header

    Given the group "qagroup" and server "qanode"
    And   the software "<software_name>" with version "<version>" is installed
    And   accept header value "<accept_header>"
    When  I generate the manifest for the node "qanode"
    Then  I obtain an "406"

    Examples:
    | accept_header     |
    | application/xml   |
    | application/lalal |
    | application/json1 |


  @auth
  Scenario Outline: Generate manifest with incorrect authentication: token
    Given the group "qagroup" and server "qanode"
    And   the software "<software_name>" with version "<version>" is installed
    And   the authentication token "<token>"
    When  I generate the manifest for the node "qanode"
    Then  I obtain an "401"

    Examples:

    | token                            |
    | hello world                      |
    | 891855f21b2f1567afb966d3ceee1295 |
    |                                  |


  @auth
  Scenario Outline: Generate manifest with incorrect authentication: token
    Given the group "qagroup" and server "qanode"
    And   the software "<software_name>" with version "<version>" is installed
    And   the authentication tenant-id "<tenant_id>"
    When  I generate the manifest for the node "qanode"
    Then  I obtain an "401"

    Examples:

    | tenant_id                        |
    | hello world                      |
    | 00001231234112                   |
    |                                  |
