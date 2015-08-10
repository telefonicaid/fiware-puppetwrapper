====================
FIWARE PuppetWrapper
====================

This is the code repository for the FIWARE_ PuppetWrapper, a component used by the
FIWARE `Software Deploy and Configuration`_ System (Sagitta_) in order to interact with the **PuppetMaster** server
using a RESTFull API. This component makes possible the installation of artifacts in VM deployed on FIWARE-Lab
using *Puppet modules*.

PuppetWrapper component is a *wrapper* for Puppet tool. This one is a model-based configuration management
solution that lets you define the state of your IT infrastructure, using the Puppet language.
More about puppet on PuppetLabs_ web page.

Any feedback about this component is highly welcome, including bugs, doc typos or things/features you think
should be included or improved. You can use `GitHub issues`_ to provide feedback or report defects.


|Build Status|    |Coverage Status|    |StackOverflow|


Overall description
===================

*PuppetWrapper* is a wrapper for PuppetMaster server that provides a RESTFull API with basic operations to manage
manifests and modules of this service. This component interact with the *Puppet manifest catalog* (filesystem) in
the PuppetMaster server, to manage node configuration using Puppet modules.

Following operation are published by PuppetWrapper API:

* Generate manifests for nodes.
* Delete manifests of nodes.
* Configure manifests for installing/uninstalling modules.
* Download modules.
* Remove modules.

These usage scenarios and the PuppetWrapper features are described in this document.

The user of this service on FIWARE is the `Software Deploy and Configuration`_ component, to configure PuppetMaster
with the required data for installing/uninstalling artifacts in deployed VMs on FIWARE-Lab platform. The configuration
is given by Blueprints through `PaaS Manager`_ (Pegasus_) provisioning layer, providing a flexible mechanism
to perform the deployment of artifacts on VMs using the Puppet configuration management solutions.

This service authenticates users against the OpenStack IdM/Keystone. User auth data should be provided in
each request to get access to API operations. Headers ``Tenant-Id`` and ``X-Auth-Token`` should be sent
as part of each request.


PuppetWrapper is a Java_ Web Application developed under the `Spring Framework`_. The servlet engine provided as
Web Server and container is Jetty_. All dependencies and project configurations are managed using Maven_.



Build and Install
=================

There are two ways of installing PuppetWrapper. You can use a *manual* installation procedure or an *automatic* one:

* Manually: You will have to install the RPM on your system after installing and configuring all requirements.
  `Installing puppet wrapper manually <./doc/installation-guide.rst#installing-puppet-wrapper-manually>`_.
* Automatic install using an script.
  `Installing puppet wrapper automatically <./doc/installation-guide.rst#installation-puppet-wrapper-via-script>`_.


PuppetWrapper must be installed in the same host as PuppetMaster.


Requirements
------------

* The reference *operating system* is CentOS 6.3
* PuppetMaster. Please, go PuppetLabs_ web page to know more about this service.
* PuppetDB. Go to puppet PuppetDB_ web page to know more about this service.
* Apache Maven_, that is a software project management and comprehension tool.
  Based on the concept of a project object model (POM), Maven can manage a project's build,
  reporting and documentation from a central piece of information.
* Git_: In order to perform the installation via script, this *SCM* should be installed.
  Take a look at `How to install Git`_ documentation.
* RPM dependencies if you install PuppetWrapper manually (some of these packages could not be in the official
  CentOS/RedHat repository but in EPEL, in which case you have to configure EPEL repositories,
  see http://fedoraproject.org/wiki/EPEL)


Installation
------------

The installation of **FIWARE PuppetWrapper** component can be done in an easy way executing the *autoinstaller script*
for CentOS platforms. This is the recommended procedure to install this software.

The installation directory by default is ``/opt/fiware-puppetwrapper/``

**Using autoinstaller script**

::

    # git clone https://github.com/telefonicaid/fiware-puppetwrapper
    # ./scripts/bootstrap/centos.sh

**Using FIWARE package repository**

The FIWARE repository URL (RPMs) is:

::

    http://repositories.testbed.fiware.org/repo/rpm/x86_64

To install PuppetWrapper from this repository, add it to yum repositories and execute

::

    # yum install fiware-puppetwrapper

**Using RPM files**

Download the RPM package you are interested on installing and run:

::

    # rpm -i fiware-puppetwrapper-X.Y.Z.noarch.rpm



To know more about PuppetWrapper installation procedure, take a look at
the `PuppetWrapper installation documentation <./doc/installation-guide.rst>`_



Running
=======

Once installed using *autoinstaller script* or following all steps in the installation documentation, you will have
configured PuppetWrapper as a service on your system. You will typically need superuser privileges to use
PuppetWrapper as a system service, so the following commands need to be run as root or using the sudo command.

All required services (mongo, puppetdb, puppetmaster, etc.) should be already started when executing:

::

    # service fiware-puppetwrapper start


To know more about PuppetWrapper execution and its prerequisites, take a look at the
`PuppetWrapper installation documentation <./doc/installation-guide.rst>`_



Configuration file
==================

The configuration file used by PuppetWrapper service is stored in the installation directory
``$PUPPETWRAPPER_HOME/webapps/puppetWrapper.properties``

An example of this file is:

::

    #puppet path
    defaultManifestsPath=/etc/puppet/manifests/
    modulesCodeDownloadPath=/etc/puppet/modules/
    defaultHieraPath=/etc/puppet/hieradata/node/
    puppetDBUrl=http://puppet-master.lab.fi-ware.org:8080

    #mongo connection
    mongo.host=127.0.0.1
    mongo.port=27017

    #others
    keystoneURL=http://cloud.lab.fi-ware.org:4731/v2.0/
    adminUser=admin
    adminPass=*********
    adminTenant=admin
    thresholdString=84000000
    cloudSystem=FIWARE

The configuration you need setup is:

* The Puppet directories where you PuppetMaster is managing the node manifests and the modules catalog.
* The path where Hiera node data is stored.
* The PuppetDB URL where this server is listening to.
* The mongoDB service.
* The OpenStack Keystone URL and admin credentials.



Checking status
===============

In order to check the status of PuppetWrapper, use the following command with superuser privileges
(using the root user or the sudo command):

::

    # service fiware-puppetwrapper status

      >> ...
      >> Jetty running pid=2247


Smoke test
----------

In order to check that PuppertWrapper is working right, please make the following request from the PuppetWrapper host:

::

    curl -v -k -H 'Content-Type:application/json' -H 'Accept:application/json' \
         -H 'X-Auth-Token: <token-id>' -H 'Tenant-Id: <tenant-id>' \
         -X POST 'https://localhost:8443/puppetwrapper/v2/node/<hostname>/install' \
         -d '{"attributes":[{"value":"att1","key":"val1","description":"ATT 1"}], \
              "version":"0.1", "group":"Testing", "softwareName":"MyTest"}'

The required params are:

* **<tenant-id>** should be a particular tenant-id user.
* **<token-id>** should be a token returned by keystone.
* **<hostname>** should be the response when executing the command 'hostname' in the virtual machine without
  the domain if exists. For testing use *testvm*

The response from the web service should be:

::

    {
     "id":"testvm",
     "groupName":"Testing",
     "softwareList":[{"name":"MyTest","version":"0.1","action":"INSTALL","attributes":[{"value":"att1","key":"val1","description":"ATT 1"}]}],
     "manifestGenerated":false
    }


To check the rest of services (PuppetMaster, PuppetDB, mongoDB, , take a look at
the `Sanity Checks documentation <./doc/installation-guide.rst#sanity-checks>`_



Testing
=======

Acceptance: Component and E2E testing
-------------------------------------

How to run these test cases, prerequisites and all related documentation is described on
`PuppetWrapper Acceptance Tests <./acceptance_tests>`_ project.

Unit tests
----------

Unittests are located in ``src/test``. To run them using Maven, execute following command from command-line:

::

    # mvn test





API Overview
============

**Prepare the installation of a module in the given hostname**

::

    curl -v -k -H 'Content-Type:application/json' -H 'Accept:application/json' \
         -H 'X-Auth-Token: <token-id>' -H 'Tenant-Id: <tenant-id>' \
         -X POST 'https://localhost:8443/puppetwrapper/v2/node/{hostname}/install' \
         -d '{"attributes":[{"value":"att1","key":"val1","description":"ATT 1"}], \
              "version":"0.1", "group":"Testing", "softwareName":"MyTest"}'


**Prepare the uninstallation of a module in the given hostname**

::

    curl -v -k -H 'Content-Type:application/json' -H 'Accept:application/json' \
         -H 'X-Auth-Token: <token-id>' -H 'Tenant-Id: <tenant-id>' \
         -X POST 'https://localhost:8443/puppetwrapper/v2/node/{hostname}/install' \
         -d '{"attributes":[{"value":"att1","key":"val1","description":"ATT 1"}], \
              "version":"0.1", "group":"Testing", "softwareName":"MyTest"}'



**Generate all required files (install/uninstall) in PuppetMaster, configuring the manifests for the given hostname**

::

    curl -v -k -H 'Content-Type:application/json' -H 'Accept:application/json' \
         -H 'X-Auth-Token: <token-id>' -H 'Tenant-Id: <tenant-id>' \
         -X GET 'https://localhost:8443/puppetwrapper/v2/node/{hostname}/generate'


Take a look at the `PuppetWrapper API reference <./doc/api.rst>`_ documentation to know
more about the RESTFull API.



Advanced topics
===============

- Installation and administration:

  - `Building PuppetWrapper from sources <./doc/installation-guide.rst#puppet-wrapper-building-instructions>`_
  - `Install puppet wrapper via Script <./doc/installation-guide.rst#installation-puppet-wrapper-via-script>`_
  - `Install puppet wrapper manually <./doc/installation-guide.rst#installing-puppet-wrapper-manually>`_
  - `Install and configure prerequisites <./doc/installation-guide.rst#installing-puppet-wrapper-manually>`_

    - `Install and configure mongoDB <./doc/installation-guide.rst#install-mongodb>`_
    - `Install and configure PuppetMaster <./doc/installation-guide.rst#install-puppet-master>`_
    - `Install and configure PuppetDB <./doc/installation-guide.rst#install-puppet-db>`_
    - `Install and configure PuppetDB <./doc/installation-guide.rst#install-puppet-db>`_
    - `Install and configure PostgreSQL <./doc/installation-guide.rst#using-postgresql>`_
    - `Install and configure Hiera <./doc/installation-guide.rst#install-hiera>`_

  - `Install and configure PuppetWrapper from RPM <./doc/installation-guide.rst#install-puppetwrapper-from-rpm>`_
  - `Configuring PuppetWrapper as a service <./doc/installation-guide.rst#configuring-the-puppetwrapper-as-service>`_
  - `Setup PuppetWrapper <./doc/installation-guide.rst#puppetwrapper-configuration-instructions>`_
  - `Configure the HTTPS certificate <./doc/installation-guide.rst#configuring-the-https-certificate>`_

- `Known issues <./doc/installation-guide.rst#known-issues>`_.
- `Sanity Checks <./doc/installation-guide.rst#sanity-checks>`_.

- API:

  - `PuppetWrapper API reference <./doc/api.rst>`_



License
=======

FIWARE PuppetWrapper is licensed under Apache v2.0 license.



.. REFERENCES

.. _FIWARE: https://www.fiware.org/
.. _Software Deploy and Configuration: https://github.com/telefonicaid/fiware-sdc
.. _Sagitta: http://catalogue.fiware.org/enablers/software-deployment-configuration-sagitta
.. _PaaS Manager: https://github.com/telefonicaid/fiware-paas
.. _Pegasus: http://catalogue.fiware.org/enablers/paas-manager-pegasus
.. _PuppetLabs: https://puppetlabs.com/puppet/puppet-open-source
.. _PuppetDB: http://docs.puppetlabs.com/puppetdb/latest/
.. _GitHub issues: https://github.com/telefonicaid/fiware-puppetwrapper/issues
.. _Java: http://www.oracle.com/technetwork/es/java/javase/downloads/index.html
.. _Spring Framework: https://spring.io/
.. _Jetty: http://www.eclipse.org/jetty/
.. _Maven: https://maven.apache.org/
.. _Git: https://git-scm.com/
.. _How to install Git: https://git-scm.com/book/en/v1/Getting-Started-Installing-Git


.. IMAGES

.. |Build Status| image::  https://travis-ci.org/telefonicaid/fiware-puppetwrapper.svg
   :target: https://travis-ci.org/telefonicaid/fiware-puppetwrapper
.. |Coverage Status| image:: https://coveralls.io/repos/telefonicaid/fiware-puppetwrapper/badge.svg?branch=develop
   :target: https://coveralls.io/r/telefonicaid/fiware-puppetwrapper
.. |StackOverflow| image:: http://b.repl.ca/v1/help-stackoverflow-orange.png
   :target: http://stackoverflow.com/questions/tagged/fiware
