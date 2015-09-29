====================
PuppetWrapper API v2
====================

This is the PuppetWrapper API reference documentation.



Auth headers
------------

* Tenant-Id: Tenant-Id of the User (OpenStack IdM/Keystone)
* X-Auth-Token: Valid auth token retrieved for this User from Keystone.



API Reference
-------------

-  POST /puppetwrapper/v2/node/{nodeName}/install

   -  json payload:
   -  {"attributes":[{"value":"valor","key":"clave","id":23119,"description":null}],"version":"0.1","group":"alberts","softwareName":"testPuppet"}

-  POST /puppetwrapper/v2/node/{nodeName}/uninstall

   -  json payload:
   -  {"attributes":[{"value":"valor","key":"clave","id":23119,"description":null}],"version":"0.1","group":"alberts","softwareName":"testPuppet"}

-  GET /puppetwrapper/v2/node/{nodeName}/generate

   -  will generate the following files in /etc/puppet/manifests
   -  add an import line to site.pp
   -  generate the corresponding .pp file as group/nodeName.pp

-  POST /puppetwrapper/module/{moduleName}/download

   -  payload : json as: {"url":”value”, ”repoSource”:”value”}
   -  Value on repoSource can be: git /svn
   -  will download the source code from the given url under
      {moduleName} directory.

-  DELETE /puppetwrapper/v2/node/{nodeName}

   -  will delete the node: nodeName

-  DELETE /puppetwrapper/v2/module/{modulename}

   -  will delete the module: moduleName
