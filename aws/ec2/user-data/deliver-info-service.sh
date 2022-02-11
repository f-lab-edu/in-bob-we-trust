#!/bin/bash
# install java
yum -y install java-11-amazon-corretto-headless

# set up cloud-watch agent
wget https://s3.ap-northeast-2.amazonaws.com/amazoncloudwatch-agent-ap-northeast-2/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm \
&& sudo rpm -U ./amazon-cloudwatch-agent.rpm

# RUN server
cd /home/ec2-user
wget https://github.com/f-lab-edu/in-bob-we-trust/releases/download/v0.1.0_pre_traffic_handle/delivery-info-service.jar
java -Dspring.data.mongodb.uri="mongodb://ec2-13-125-50-55.ap-northeast-2.compute.amazonaws.com:27017/?readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false" \
-Xms1024m -Xmx2048m \
-DrestClient.proxy.baseUrl="alb-delivery-relay-service-1071471430.ap-northeast-2.elb.amazonaws.com" \
-Dhello.sha="z+++" \
-jar ./delivery-info-service.jar \
--spring.profiles.active=production  > /dev/null 2>&1 & disown

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/bin/config.json -s


sudo lsof -t -i tcp:8888 | xargs kill && \
cd /home/ec2-user \
&& java -Dspring.data.mongodb.uri="mongodb://ec2-13-125-50-55.ap-northeast-2.compute.amazonaws.com:27017/?readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false" \
-Xms1024m -Xmx2048m \
-DrestClient.proxy.baseUrl="alb-delivery-relay-service-1071471430.ap-northeast-2.elb.amazonaws.com" \
-Dhello.sha="z+++" \
-jar ./delivery-info-service.jar \
--spring.profiles.active=production  > /dev/null 2>&1 & disown
