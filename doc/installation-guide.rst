Installing puppet wrapper
=========================

Install mongodb
---------------

Create a /etc/yum.repos.d/mongodb.repo file to hold the following
configuration information for the MongoDB repository: 64-bit system:

.. code::

     [mongodb]
     name=MongoDB Repository
     baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64/
     gpgcheck=0
     enabled=1

32-bit system: 

.. code::
     
     [mongodb]
     name=MongoDB Repository
     baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/i686/
     gpgcheck=0
     enabled=1

On Centos execute as root:

.. code::

     sudo yum install -y mongodb-org

-  change mongo configuration

add in /etc/mongod.conf:

.. code::

     smallfiles=true

Start the mongodb

.. code::

     sudo service mongod start

Install puppet master
---------------------

-  on Centos run these commands as root:

.. code ::
     
     sudo rpm -ivh https://yum.puppetlabs.com/el/6/products/x86_64/puppetlabs-release-6-7.noarch.rpm
     yum install puppet
     puppet master
     sudo puppet apply -e 'service { "puppet": enable => true, }'
     chmod -R 777 /etc/puppet/manifests
     chmod -R 777 /etc/puppet/modules
     
-  create file **/etc/puppet/autosign.conf** - master's whitelist - with
   content like:

.. code ::

     *.novalocal
     *.openstacklocal

Install puppet db
-----------------

The puppet master needs to have the puppetdb installed and configured in
order to be able to persistent all data in a database. To do that

.. code ::

     yum install puppetdb puppetdb-terminus
     chkconfig puppetdb on

configure Puppet master to use storeconfigs

-  vi /etc/puppet/puppet.conf and add following into [master] section:

.. code ::

     storeconfigs = true
     storeconfigs_backend = puppetdb
     
Configure PuppetDB to use the correct puppet master hostname and port

-  vi /etc/puppet/puppetdb.conf and add following into [main] section.

.. code ::

     server = your-server-name
     port = 8081

Note that the your-server-name used has to resolve via DNS, or otherwise
add it in puppet agent hosts in /etc/hosts If the server name is other
than the hostname, a puppet master configuration change will be needed
in puppet.conf, a certname value must be defined (see puppet
documentation)

-  Restart Puppet master to apply settings (Note: these operations may
   take about two minutes. You can ensure that PuppetDB is running by
   executing telnet your-domain-name 8081):

Restart puppet master process, then:

.. code ::

     puppetdb-ssl-setup  (or puppetdb ssl-setup)

Restart puppet master process, then:

.. code ::

     service puppetdb restart

Using PostgreSQL
^^^^^^^^^^^^^^^^

Install postgreSQL (on Centos as root)

.. code ::

     yum install postgresql-server
     service postgresql initdb
     service postgresql start

Before using the PostgreSQL backend, you must set up a PostgreSQL
server, ensure that it will accept incoming connections, create a user
for PuppetDB to use when connecting, and create a database for PuppetDB.
Completely configuring PostgreSQL is beyond the scope of this manual,
but if you are logged in as root on a running Postgres server, you can
create a user and database as follows:

.. code ::

     sudo -u postgres sh
     createuser -DRSP puppetdb
     createdb -E UTF8 -O puppetdb puppetdb
     exit

If you are running PostgreSQL 9.3 or above you should install the regexp
optimized index extension pg\_trgm:

.. code ::

     sudo -u postgres sh
     psql puppetdb -c 'create extension pg_trgm'
     exit

Next you will most likely need to modify the pg\_hba.conf file to allow
for md5 authentication from at least localhost. To locate the file you
can either issue a locate pg\_hba.conf command (if your distribution
supports it) or consult your distributionâ€™s documentation for the
PostgreSQL confdir.

The following example pg\_hba.conf file allows md5 authentication from
localhost for both IPv4 and IPv6 connections:

.. code::

     #TYPE DATABASE USER CIDR-ADDRESS METHOD
     local all      all                md5
     host  all      all  127.0.0.1/32  md5
     host  all      all  ::1/128       md5

Restart PostgreSQL and ensure you can log in by running:

.. code ::

     $ sudo service postgresql restart
     $ psql -h localhost puppetdb puppetdb

To configure PuppetDB to use this database, put the following in the
[database] section in file puppetdb.conf:

.. code ::

     classname = org.postgresql.Driver 
     subprotocol = postgresql 
     subname = //<HOST>:<PORT>/<DATABASE> 
     username = <USERNAME>
     password = <PASSWORD> 

Replace <HOST> with the DB server’s hostname. Replace <PORT> with the port on which PostgreSQL is listening.
Replace <DATABASE> with the name of the database you’ve created for use with PuppetDB.

Install hiera
-------------

-  install hiera package

.. code ::

     sudo puppet resource package hiera ensure=installed

-  install puppet functions

.. code ::

     sudo puppet resource package hiera-puppet ensure=installed

Note: If you are using Puppet 3 or later, you probably already have
Hiera installed. You can skip the above steps, and go directly to the
following:

-  execute

.. code ..

     cd /etc/puppet
     mkdir hieradata
     cd hieradata
     mkdir node`

-  create $confdir/hiera.yaml (normally /etc/puppet/hiera.yaml) with
   content:

.. code ::

     :backends:
     - yaml
     :yaml:
     :datadir: /etc/puppet/hieradata
     :hierarchy:
     - "node/%{::fqdn}"
     - common

Install PuppetWtapper from RPM
------------------------------
  
The PuppetWrapper is packaged as RPM and stored in the rpm repository. Thus, the first thing to do is to create a file 
in /etc/yum.repos.d/fiware.repo, with the following content.

 .. code::
 
	[Fiware]
	name=FIWARE repository
	baseurl=http://repositories.testbed.fi-ware.eu/repo/rpm/x86_64/
	gpgcheck=0
	enabled=1
    
After that, you can install the SDC just doing:

.. code::

	yum install fiware-puppetwrapper

or specifying the version

.. code::

	yum install fiware-wrapper-{version}-1.noarch

to install a specific PuppetWrapper version where {version} could be "3.3.0"

Puppet Wrapper Building instructions
------------------------------------

Requirements: To install Puppet Wrapper from source it is required to have the following software installed in your host
previously:

- git

- java 1.7

- maven

Here we include a small guide to install the required software. If you find any problem in the installation process,
please refer to the official sites:

Install git

.. code::

   sudo yum install git

Install java 1.7

.. code::

   sudo yum install java-1.7.0-openjdk-devel

Install maven 2.5

.. code::

	sudo yum install wget
	wget http://mirrors.gigenet.com/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
	su -c "tar -zxvf apache-maven-3.2.5-bin.tar.gz -C /usr/local"
	cd /usr/local
	sudo ln -s apache-maven-3.2.5 maven

Add the following lines to the file /etc/profile.d/maven.sh

.. code::

	# Add the following lines to maven.sh
	export M2_HOME=/usr/local/maven
	export M2=$M2_HOME/bin
	PATH=$M2:$PATH

In order to check that your maven installation is OK, you shluld exit your current session with "exit" command, enter again
and type

.. code::

	mvn -version

if the system shows the current maven version installed in your host, you are ready to continue with this guide.

Now we are ready to build the SDC rpm and finally install it

The SDC is a maven application so, we should follow following instructions:

- Download SDC code from github

.. code::

   git clone -b develop https://github.com/telefonicaid/fiware-puppetwrapper

- Go to fiware-sdc folder and compile, launch test and build all modules

.. code::
	
    cd fiware-puppetwrapper/
    mvn clean install
    
-  Compile, launch test and build all modules

.. code ::
     
     $ mvn assembly:assembly

- for centOS (you need to have installed rpm-bluid. If not, please type "yum install rpm-build" )

.. code::

    mvn install -Prpm -DskipTests
        (created target/rpm/paasmanager/RPMS/noarch/paasmanager-XXXX.noarch.rpm)

Finally go to the folder where the rpm has been created (target/rpm/fiware-paas/RPMS/noarch) and execute

.. code::

	cd target/rpm/fiware-paas/RPMS/noarch
	rpm -i <rpm-name>.rpm
	

Configuring the PuppetWrapper as service 
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Once we have installed and configured the puppetwapper, the next step is to configure it as a service.
To do that just create a file in /etc/init.d/fiware-puppetwrapper with the following content

.. code::

    #!/bin/bash
    # chkconfig: 2345 20 80
    # description: Description comes here....
    # Source function library.
    . /etc/init.d/functions
    start() {
        /opt/fiware-puppetwrapper/bin/jetty.sh start
    }
    stop() {
        /opt/fiware-puppetwrapper/bin/jetty.sh stop
    }
    case "$1" in 
        start)
            start
        ;;
        stop)
            stop
        ;;
        restart)
            stop
            start
        ;;
        status)
            /opt/fiware-puppetwrapper/bin/jetty.sh status
        ;;
        *)
            echo "Usage: $0 {start|stop|status|restart}"
    esac
    exit 0 

Now you need to execute:

.. code::

    chkconfig --add fiware-puppetwrapper
    chkconfig fiware-puppetwrapper on
    service fiware-puppetwrapper start
 
 
PuppetWrapper Configuration instructions
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

file puppetwrapper.properties contains all necessary parameters.

.. code ::

     #puppet path
     defaultManifestsPath=/etc/puppet/manifests/
     modulesCodeDownloadPath=/etc/puppet/modules/
     #mongo connection
     mongo.host=127.0.0.1
     mongo.port=27017

and also the correct values to connect to the keystone:

.. code ::

     #others
     keystoneURL=<the keystone url>
     adminUser=<the admin user>
     adminPass=<the admin password>
     adminTenant=00000000000000000000000000001 

To allow puppetwrapper to execute add to /etc/sudoers:

.. code ::
     
     tomcat ALL=(ALL) NOPASSWD: /usr/bin/puppet

in this section

.. code ::

     ## Allows people in group wheel to run all commands
     # %wheel ALL=(ALL) ALL
     ## Same thing without a password
     # %wheel ALL=(ALL) NOPASSWD: ALL

comment out the following line

.. code ::

     #Defaults requiretty
     PuppetWrapper API

Configuring the HTTPS certificate
---------------------------------

The service is configured to use HTTPS to secure the communication between clients and the server. One central point in HTTPS security is the certificate which guarantee the server identity.

Quickest solution: using a self-signed certificate
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The service works "out of the box" against passive attacks (e.g. a sniffer) because a self-signed certificated is generated automatically when the RPM is installed. Any certificate includes a special field call "CN" (Common name) with the identity of the host: the generated certificate uses as identity the IP of the host.

The IP used in the certificate should be the public IP (i.e. the floating IP). The script which generates the certificate knows the public IP asking to an Internet service (http://ifconfig.me/ip). Usually this obtains the floating IP of the server, but of course it wont work without a direct connection to Internet.

If you need to regenerate a self-signed certificate with a different IP address (or better, a convenient configured hostname), please run:

.. code::

    /opt/fiware-puppetwrapper/bin/generateselfsigned.sh myhost.mydomain.org

By the way, the self-signed certificate is at /etc/keystorejetty. This file wont be overwritten although you reinstall the package. The same way, it wont be removed automatically if you uninstall de package.

Advanced solution: using certificates signed by a CA
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Although a self-signed certificate works against passive attack, it is not enough by itself to prevent active attacks, specifically a "man in the middle attack" where an attacker try to impersonate the server. Indeed, any browser warns user against self-signed certificates. To avoid these problems, a certificate conveniently signed by a CA may be used.

If you need a certificate signed by a CA, the more cost effective and less intrusive practice when an organization has several services is to use a wildcard certificate, that is, a common certificate among all the servers of a DNS domain. Instead of using an IP or hostname in the CN, an expression as ".fiware.org" is used.

This solution implies:

* The service must have a DNS name in the domain specified in the wildcard certificate. For example, if the domain is ".fiware.org" a valid name may be "puppetwrapper.fiware.org".
* The clients should use this hostname instead of the IP
* The file /etc/keystorejetty must be replaced with another one generated from the wildcard certificate, the corresponding private key and other certificates signing the wild certificate.

It's possible that you already have a wild certificate securing your portal, but Apache server uses a different file format. A tool is provided to import a wildcard certificate, a private key and a chain of certificates, into /etc/keystorejetty:

.. code::

     # usually, on an Apache installation, the certificate files are at /etc/ssl/private
     /opt/fiware-puppetwrapper/bin/importcert.sh key.pem cert.crt chain.crt

If you have a different configuration, for example your organization has got its own PKI, please refer to: http://docs.codehaus.org/display/JETTY/How%2bto%2bconfigure%2bSSL
 

Known issues
^^^^^^^^^^^^

-  When a puppet manifest is executed and the execution of a module fails, in the case where there's more than 1 module installed we don't know wich one has failed -> this information granularity is not provided by puppetdb, so we can't delete the module that caused the error in the manifest

-  On instalation, a task finished on success even though the manifest execution has failed. We rely on the "catalog_timestamp" value that indicates a catalog execution. It does not tell whether the execution was correct or not. In fact even when the execution fails, the "catalog_timestamp" value is updated.

