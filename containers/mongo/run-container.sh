sudo docker run \
 -p "27017:27017" \
 -v /home/ec2-user/mongo/data:/data/db \
  mongo   > /dev/null 2>&1 & disown