#!/bin/bash
# $1: key
# $2: cert
# $3: chain
if [ $# -ne 3 ] ; then
  echo "Use $0 <keyfile> <certfile> <chainfile>"
  exit
fi
cat $2 $3 > fullchain.crt
openssl pkcs12 -inkey $1 -in fullchain.crt -export -out jetty.pkcs12 -password pass:password
keytool -importkeystore -srckeystore jetty.pkcs12 -srcstoretype PKCS12 -destkeystore /etc/keystorejetty.new -srcstorepass password -deststorepass password -destalias jetty -alias 1
chmod 640 /etc/keystorejetty
rm fullchain.crt jetty.pkcs12
if [ -f /etc/keystorejetty.new ] ; then
   mv /etc/keystorejetty.new /etc/keystorejetty
   echo "content imported into /etc/keystorejetty"
fi
