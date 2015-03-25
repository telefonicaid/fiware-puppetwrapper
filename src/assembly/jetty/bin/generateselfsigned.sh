#!/bin/bash
if [ $# -eq 1 ] ; then
  ip=$1
else
  ip=$(/usr/bin/curl -m15 -s http://ifconfig.me/ip)
  if ! echo $ip |grep -E [0-9]+\.[0-9]+\.[0-9]+\.[0-9]+ >/dev/null ; then
     ip=$(hostname -i)
  fi
  if echo $ip |grep  ^127 >/dev/null ; then
     defaultdev=$(/sbin/route  | awk  '/^default/ { print $8}')
     ip=$(ip addr show dev $defaultdev |awk '/inet / { X = split($2, A, "/") ; print A[1]}')
  fi
fi
echo "Generating certificate with CN=$ip"
keytool -genkey -keyalg RSA -alias jetty -keystore /etc/keystorejetty.new -validity 730 -keypass password -storepass password -dname "CN=$ip, O=fiware" -keysize 2048

if [ -f /etc/keystorejetty.new ] ; then
   mv /etc/keystorejetty.new /etc/keystorejetty
   echo "key and certificate generated in /etc/keystorejetty"
fi
