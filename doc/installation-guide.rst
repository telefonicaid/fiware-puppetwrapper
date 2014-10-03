Installing puppet wrapper
-------------------------

Install mongodb
~~~~~~~~~~~~~~~

Create a /etc/yum.repos.d/mongodb.repo file to hold the following
configuration information for the MongoDB repository: 64-bit system:

| ``[mongodb]``
| ``name=MongoDB Repository``
| ``baseurl=``\ ```http://downloads-distro.mongodb.org/repo/redhat/os/x86_64/`` <http://downloads-distro.mongodb.org/repo/redhat/os/x86_64/>`__
| ``gpgcheck=0``
| ``enabled=1``

32-bit system:

| ``[mongodb]``
| ``name=MongoDB Repository``
| ``baseurl=``\ ```http://downloads-distro.mongodb.org/repo/redhat/os/i686/`` <http://downloads-distro.mongodb.org/repo/redhat/os/i686/>`__
| ``gpgcheck=0``
| ``enabled=1``

On Centos execute as root:

``sudo yum install -y mongodb-org``

-  change mongo configuration

add in /etc/mongod.conf:

``smallfiles=true``

Start the mongodb

``sudo service mongod start``

Install puppet master
~~~~~~~~~~~~~~~~~~~~~

-  on Centos run these commands as root:

| ``sudo rpm -ivh https://yum.puppetlabs.com/el/6/products/x86_64/puppetlabs-release-6-7.noarch.rpm``
| ``yum install puppet``
| ``puppet master``
| ``sudo puppet apply -e 'service { "puppet": enable => true, }'``
| ``chmod -R 777 /etc/puppet/manifests``
| ``chmod -R 777 /etc/puppet/modules``

-  create file **/etc/puppet/autosign.conf** - master's whitelist - with
   content like:

| ``*.novalocal``
| ``*.openstacklocal``

Install puppet db
-----------------

The puppet master needs to have the puppetdb installed and configured in
order to be able to persistent all data in a database. To do that

| `` yum install puppetdb puppetdb-terminus``
| `` chkconfig puppetdb on``

configure Puppet master to use storeconfigs

-  vi /etc/puppet/puppet.conf and add following into [master] section:

| ``storeconfigs = true``
| ``storeconfigs_backend = puppetdb``

Configure PuppetDB to use the correct puppet master hostname and port

-  vi /etc/puppet/puppetdb.conf and add following into [main] section.

| ``server = your-server-name``
| ``port = 8081``

Note that the your-server-name used has to resolve via DNS, or otherwise
add it in puppet agent hosts in /etc/hosts If the server name is other
than the hostname, a puppet master configuration change will be needed
in puppet.conf, a certname value must be defined (see puppet
documentation)

-  Restart Puppet master to apply settings (Note: these operations may
   take about two minutes. You can ensure that PuppetDB is running by
   executing telnet your-domain-name 8081):

Restart puppet master process, then:

``puppetdb-ssl-setup``

Restart puppet master process, then:

``service puppetdb restart``

Using PostgreSQL
~~~~~~~~~~~~~~~~

Install postgreSQL (on Centos as root)

| `` yum install postgresql-server``
| `` service postgresql initdb``
| `` service postgresql start``

Before using the PostgreSQL backend, you must set up a PostgreSQL
server, ensure that it will accept incoming connections, create a user
for PuppetDB to use when connecting, and create a database for PuppetDB.
Completely configuring PostgreSQL is beyond the scope of this manual,
but if you are logged in as root on a running Postgres server, you can
create a user and database as follows:

| `` sudo -u postgres sh``
| `` createuser -DRSP puppetdb``
| `` createdb -E UTF8 -O puppetdb puppetdb``
| `` exit``

If you are running PostgreSQL 9.3 or above you should install the regexp
optimized index extension pg\_trgm:

| `` sudo -u postgres sh``
| `` psql puppetdb -c 'create extension pg_trgm'``
| `` exit``

Next you will most likely need to modify the pg\_hba.conf file to allow
for md5 authentication from at least localhost. To locate the file you
can either issue a locate pg\_hba.conf command (if your distribution
supports it) or consult your distribution’s documentation for the
PostgreSQL confdir.

The following example pg\_hba.conf file allows md5 authentication from
localhost for both IPv4 and IPv6 connections:

#. TYPE DATABASE USER CIDR-ADDRESS METHOD
#. local all all md5
#. host all all 127.0.0.1/32 md5
#. host all all ::1/128 md5

Restart PostgreSQL and ensure you can log in by running:

| `` $ sudo service postgresql restart``
| `` $ psql -h localhost puppetdb puppetdb``

To configure PuppetDB to use this database, put the following in the
[database] section in file puppetdb.conf:

classname = org.postgresql.Driver subprotocol = postgresql subname =
//:/ username = password = Replace with the DB server’s hostname.
Replace with the port on which PostgreSQL is listening. Replace with the
name of the database you’ve created for use with PuppetDB.

Install hiera
-------------

-  install hiera package

``sudo puppet resource package hiera ensure=installed``

-  install puppet functions

``sudo puppet resource package hiera-puppet ensure=installed``

Note: If you are using Puppet 3 or later, you probably already have
Hiera installed. You can skip the above steps, and go directly to the
following:

-  execute

| ``cd /etc/puppet``
| ``mkdir hieradata``
| ``cd hieradata``
| ``mkdir node``

-  create $confdir/hiera.yaml (normally /etc/puppet/hiera.yaml) with
   content:

| ``:backends:``
| ``  - yaml``
| ``:yaml:``
| ``  :datadir: /etc/puppet/hieradata``
| ``:hierarchy:``
| ``  - "node/%{::fqdn}"``
| ``  - common``

Wrapper Building instructions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It is a a maven application:

-  Compile, launch test and build all modules

``       $ mvn assembly:assembly``

-  copy target/distribution/puppwrapper-dist to a desired location
-  run ./jetty.sh start on puppetwrapper-dist/bin
-  Jetty will run by default on port 8082

-  Configuration instructions

file puppetwrapper.properties contains all necessary parameters.

| ``       #puppet path``
| ``       defaultManifestsPath=/etc/puppet/manifests/``
| ``       modulesCodeDownloadPath=/etc/puppet/modules/``
| ``       #mongo connection``
| ``       mongo.host=127.0.0.1``
| ``       mongo.port=27017``
-  

   -  in this section

| ``## Allows people in group wheel to run all commands``
| ``# %wheel        ALL=(ALL)       ALL``
| ``## Same thing without a password``
| ``# %wheel        ALL=(ALL)       NOPASSWD: ALL``

-  

   -  comment out the following line

``#Defaults    requiretty``

