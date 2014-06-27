#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
import os.path
import xml.dom.minidom
import argparse



class Config():

    def __add_server(self, parent_node, settings, server_name):
        try:
            os.environ["TRAVIS_SECURE_ENV_VARS"]
        except KeyError:
            print "no secure env vars available, please declare it first"
            sys.exit()

        serversNodes = settings.getElementsByTagName("servers")
        if not serversNodes:
            serversNode = parent_node.createElement("servers")
            settings.appendChild(serversNode)
        else:
            serversNode = serversNodes[0]

        sonatypeServerNode = parent_node.createElement("server")
        sonatypeServerId = parent_node.createElement("id")
        sonatypeServerUser = parent_node.createElement("username")
        sonatypeServerPass = parent_node.createElement("password")

        idNode = parent_node.createTextNode(server_name)
        userNode = parent_node.createTextNode(os.environ["SONATYPE_USERNAME"])
        passNode = parent_node.createTextNode(os.environ["SONATYPE_PASSWORD"])

        sonatypeServerId.appendChild(idNode)
        sonatypeServerUser.appendChild(userNode)
        sonatypeServerPass.appendChild(passNode)

        sonatypeServerNode.appendChild(sonatypeServerId)
        sonatypeServerNode.appendChild(sonatypeServerUser)
        sonatypeServerNode.appendChild(sonatypeServerPass)

        serversNode.appendChild(sonatypeServerNode)

    def __add_mirror(self, parent_node, settings):
        mirrors = parent_node.createElement("mirrors")
        settings.appendChild(mirrors)

        mirror = parent_node.createElement("mirror")
        mirror_id = parent_node.createElement("id")
        mirror_id_text = parent_node.createTextNode("nexus")
        mirror_mirrorOf = parent_node.createElement("mirrorOf")
        mirror_mirrorOf_text = parent_node.createTextNode("*")
        mirror_url = parent_node.createElement("url")
        mirror_url_value = parent_node.createTextNode("http://130.206.80.169/nexus/content/groups/public")

        mirrors.appendChild(mirror)
        mirror_id.appendChild(mirror_id_text)
        mirror_mirrorOf.appendChild(mirror_mirrorOf_text)
        mirror_url.appendChild(mirror_url_value)

        mirror.appendChild(mirror_id)
        mirror.appendChild(mirror_mirrorOf)
        mirror.appendChild(mirror_url)

    def configure_server(self, server=True, mirrors=True, home_dir=os.path.expanduser("~")):

        m2 = xml.dom.minidom.parse(home_dir + '/.m2/settings.xml')
        settings = m2.getElementsByTagName("settings")[0]

        if mirrors:
            self.__add_mirror(m2, settings)
        if server:
            self.__add_server(m2, settings, "repo-release")
            self.__add_server(m2, settings, "repo-snapshot")

        m2Str = m2.toxml()
        f = open(home_dir + '/.m2/settings.xml', 'w')
        f.write(m2Str)
        f.close()


def main(prog_args):
    parser = argparse.ArgumentParser()
    parser.add_argument("--deploy", help="add servers tag to settings.xml", action="store_true")
    parser.add_argument("--mirrors", help="add mirrors tag to settings.xml", action="store_true")
    args = parser.parse_args()
    config = Config()
    config.configure_server(server=args.deploy, mirrors=args.mirrors)

if __name__ == "__main__":
    sys.exit(main(sys.argv))


