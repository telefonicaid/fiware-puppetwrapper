fiware-puppetwrapper
===================

SDC wrapper in order to deploy artifacts following puppet recipes.

Requisites:
- MongoDB
- Tomcat 7.X.X

## Building instructions
It is a a maven application:

- Compile, launch test and build all modules

        $ mvn clean install
        
## Configurations instructions
file puppetwrapper.properties contains all necessary parameters.

        #puppet path
        defaultManifestsPath=/etc/puppet/manifests/
        modulesCodeDownloadPath=/etc/puppet/modules/
        #mongo connection
        mongo.host=127.0.0.1
        mongo.port=27017
        
To allow puppetwrapper to execute add to /etc/sudoers: 

        tomcat ALL=(ALL) NOPASSWD: /usr/bin/puppet

in this section 

        ## Allows people in group wheel to run all commands
        # %wheel        ALL=(ALL)       ALL
        ## Same thing without a password
        # %wheel        ALL=(ALL)       NOPASSWD: ALL

comment out the following line 

        #Defaults    requiretty


## Wrapper API

    POST /v2/node/{nodeName}/install
        json payload:
        {"group":"value","softwareName":"value","version":"value"} 
    POST /v2/node/{nodeName}/uninstall
        json payload:
        {"group":"value","softwareName":"value","version":"value"} 
    GET /v2/node/{nodeName}/generate
        will generate the following files in /etc/puppet/manifests
        add an import line to site.pp
        generate the corresponding .pp file as group/nodeName.pp 
    POST /module/{moduleName}/download
        payload : json as: {"url":”value”, ”repoSource”:”value”}
        Value on repoSource can be: git /svn
        will download the source code from the given url under {moduleName} directory. 
    DELETE /v2/node/{nodeName}
        will delete the node: nodeName 
    DELETE /v2/module/{modulename}
        will delete the module: moduleName 


