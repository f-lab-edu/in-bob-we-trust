#!/bin/bash

cat << EOF > /etc/yum.repos.d/mongodb-org-5.0.repo
[mongodb-org-5.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/amazon/2/mongodb-org/5.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-5.0.asc
EOF

sudo yum install -y mongodb-org

# MODIFY mongod.conf file
# MODIFY mongod.conf file
# MODIFY mongod.conf file
# MODIFY mongod.conf file
# MODIFY mongod.conf file
# MODIFY mongod.conf file
# MODIFY mongod.conf file
