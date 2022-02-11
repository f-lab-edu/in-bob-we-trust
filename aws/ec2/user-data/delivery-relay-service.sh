#!/bin/bash
yum -y install java-11-amazon-corretto-headless
wget https://github.com/f-lab-edu/in-bob-we-trust/releases/download/v0.1.0_pre_traffic_handle/delivery-relay-service.jar
java -Dspring.data.mongodb.uri="mongodb://ec2-13-125-50-55.ap-northeast-2.compute.amazonaws.com:27017/?readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false" -Dfile.encoding=UTF-8 -jar ./delivery-relay-service.jar

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/bin/config.json -s

sudo lsof -t -i tcp:8090 | xargs kill \
&& cd / \
&& java -Dspring.data.mongodb.uri="mongodb://ec2-13-125-50-55.ap-northeast-2.compute.amazonaws.com:27017/?readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false" -Dfile.encoding=UTF-8 -jar ./delivery-relay-service.jar  > /dev/null 2>&1 & disown







 curl http://localhost:8090/swagger-ui.html