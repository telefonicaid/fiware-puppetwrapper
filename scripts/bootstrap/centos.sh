#!/bin/sh

echo "Checking Linux Distribution before installing PuppetWrapper"

yum install -y  redhat-lsb

linux_distro=` lsb_release -a | grep ID | awk -F " " '{print $3}'`

echo "$linux_distro"

if [ "$linux_distro" != "CentOS" ]; then
        echo "This script only works for CentOS linux distributions"
        exit
fi

echo "Installing mongodb .."
REPOFILE="/etc/yum.repos.d/mongodb.repo"

numberOfBits=`uname -m`

if [ "$numberOfBits" == "x86_64" ]; then
/bin/cat <<EOM >$REPOFILE
[mongodb]
name=MongoDB Repository
baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64/
gpgcheck=0
enabled=1
EOM
else
/bin/cat <<EOM >$REPOFILE
[mongodb] 
name=MongoDB Repository 
baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/i686/  
gpgcheck=0 
enabled=1 
EOM
fi

yum install -y mongodb-org
echo "Configuring mongodb... "
echo "smallfiles=true" > /etc/mongodb.conf
echo "Starting mongodb"
service mongod start

echo "Installing Puppet Master ... "

sudo rpm -ivh https://yum.puppetlabs.com/el/6/products/x86_64/puppetlabs-release-6-7.noarch.rpm
yum install -y puppet
puppet master
sudo puppet apply -e 'service { "puppet": enable => true, }'
chmod -R 777 /etc/puppet/manifests
chmod -R 777 /etc/puppet/modules

echo "Configuring Puppet Master "

AUTOSIGN_FILE="/etc/puppet/autosign.conf"

/bin/cat <<EOM >$AUTOSIGN_FILE
*.novalocal
*.openstacklocal
EOM

echo "Install Puppet db"
yum install -y puppetdb puppetdb-terminus
chkconfig puppetdb on

echo "Configuring puppet"
PUPPETCONF_FILE="/etc/puppet/puppet.conf"

/bin/cat <<EOM >$PUPPETCONF_FILE
[master]
storeconfigs = true
storeconfigs_backend = puppetdb
EOM

PUPPETCONFDB_FILE="/etc/puppet/puppetdb.conf"

hostname=`hostname`

/bin/cat <<EOM >$PUPPETCONFDB_FILE
[main]
server = $hostname
port = 8081
EOM

puppetdb ssl-setup

echo "Starting puppet db"
service puppetdb restart

echo "Installing Postgres"
yum install -y postgresql-server
service postgresql initdb
service postgresql start

echo "Modifying pg_hba.conf"
pg_hba_file=`find / -name pg_hba.conf`
sed -i 's/ident/md5/g' $pg_hba_file

echo "Modifying postgresql.conf"
postgresql_file=`find / -name postgresql.conf`
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '0.0.0.0'/g" $postgresql_file

echo "Configuring Postgres"

db_user=
db_passw=
db_name=
echo -n "Enter puppetdb name [puppetdb] > "
read db_name
echo -n "Enter puppetdb user [puppetdb] > "
read db_user
echo -n "Enter puppetdb password [puppetdb] > "
read db_passwd

echo "puppetdb name:$db_name"
echo "puppetdb user:$db_user"
echo "puppetdb password:$db_passwd"

sudo -u postgres sh << EOF1
psql postgres postgres << EOF
alter user postgres with password 'postgres';
create database $db_name;
create user $db_user;
alter user $db_user with password '$db_passw';
grant all privileges on database $db_name to $db_user;
\q
EOF
EOF1


echo "Checking if Postgres version is equal or above 9.3"
postgres_version=`postgres -V | awk '{print $NF}'`
version_list=`echo "9.3 $postgres_version" | sort -n`
second_element=`echo $version_list | awk '{print $2}'`
echo "$second_element"

if [ "$second_element" == "9.3" ]; then
        echo "Postgres Version is equal or above 9.3. Executing command"
        sudo -u postgres sh << EOF2
        psql puppetdb -c 'create extension pg_trgm'
EOF2
else
        echo "Postgres Version is below 9.3."
fi

echo "Restarting PostGres"
service postgresql restart

echo "Connecting database with Puppet db"
echo "Database Name=$db_name"
echo "Database Username=$db_user"
echo "Database password=$db_passwd"

/bin/cat <<EOM >>$PUPPETCONFDB_FILE
[database]
classname = org.postgresql.Driver 
subprotocol = postgresql 
subname = //localhost:5432/$db_name 
username = $db_user
password = $db_passwd
EOM

echo "Installing hiera"
puppet resource package hiera ensure=installed
puppet resource package hiera-puppet ensure=installed

echo "Creating hieradata/node directory"
cd /etc/puppet
mkdir hieradata
cd hieradata
mkdir node

echo "Creating hiera.yaml file"

HIERAYAML_FILE="/etc/puppet/hiera.yaml"

/bin/cat <<EOM >>$HIERAYAML_FILE
:backends:
- yaml
:yaml:
:datadir: /etc/puppet/hieradata
:hierarchy:
- "node/%{::fqdn}"
- common
EOM

echo "Installing PuppetWrapper"
FIWARE_REPOFILE="/etc/yum.repos.d/fiware.repo"

/bin/cat <<EOM >$FIWARE_REPOFILE
[Fiware]
name=FIWARE repository
baseurl=http://repositories.testbed.fi-ware.eu/repo/rpm/x86_64/
gpgcheck=0
enabled=1
EOM

yum install -y fiware-puppetwrapper

echo "PuppetWrapper being a service"
PUPPETWRAPPER_SERVICEFILE="/etc/init.d/fiware-puppetwrapper"

/bin/cat <<EOM >$PUPPETWRAPPER_SERVICEFILE
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
    case "\$1" in 
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
EOM

chmod 777 $PUPPETWRAPPER_SERVICEFILE

echo "Starting PuppetWrapper as a Service"

chkconfig --add fiware-puppetwrapper
chkconfig fiware-puppetwrapper on
service fiware-puppetwrapper start

echo "Configuring Puppet Wrapper"

keystone_url=
keystone_user=
keystone_passwd=
tenant_id=
puppetDBUrl=

echo -n "Enter Keystone url > "
read keystone_url
echo -n "Enter Keystone user > "
read keystone_user
echo -n "Enter Keystone passwd > "
read keystone_passwd
echo -n "Enter Admin Tenant ID > "
read tenant_id
echo -n "Enter PuppetDB url > "
read puppetDBUrl

echo "keystone_url:$keystone_url"
echo "keystone_user:$keystone_user"
echo "keystone_passwd:$keystone_passwd"
echo "tenant_id:$tenant_id"
echo "puppetDBUrl:$puppetDBUrl"

echo "Modifying puppetWrapper.properties"
puppetWrapperProperties_file="/opt/fiware-puppetwrapper/webapps/puppetWrapper.properties"
sed -i "s/keystoneURL=http:\/\/130.206.80.57:4731\/v2.0\//keystoneURL=$keystone_url/g" $puppetWrapperProperties_file
sed -i "s/adminUser=admin/adminUser=$keystone_user/g" $puppetWrapperProperties_file
sed -i "s/adminPass=8fa3c69e4c3e9fafa61/adminPass=$keystone_passwd/g" $puppetWrapperProperties_file
sed -i "s/puppetDBUrl=http:\/\/puppet-master.dev-havana.fi-ware.org:8080/puppetDBUrl=$puppetDBUrl/g" $puppetWrapperProperties_file
sed -i "s/adminTenant=admins/adminTenant=$tenant_id/g" $puppetWrapperProperties_file

service fiware-puppetwrapper stop
service fiware-puppetwrapper start

echo "Modifying sudoers"
SUDOERS_FILE="/etc/sudoers"

sed -i 's/Defaults    requiretty/#Defaults    requiretty/g' $SUDOERS_FILE

/bin/cat <<EOM >>$SUDOERS_FILE
tomcat ALL=(ALL) NOPASSWD: /usr/bin/puppet
EOM

echo "End of installation"




