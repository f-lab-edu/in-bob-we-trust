sudo yum install dockerd

sudo dockerd > /dev/null 2>&1 & disown


sudo docker run \
 -p "27017:27017" \
   -e MONGO_INITDB_ROOT_USERNAME=admin \
   -e MONGO_INITDB_ROOT_PASSWORD=1234 \
 -v /home/ec2-user/mongo/data:/data/db \
  mongo   > /dev/null 2>&1 & disown


