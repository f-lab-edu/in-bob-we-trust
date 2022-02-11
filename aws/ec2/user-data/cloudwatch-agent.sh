#!/bin/bash
wget https://s3.ap-northeast-2.amazonaws.com/amazoncloudwatch-agent-ap-northeast-2/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm \
&& sudo rpm -U ./amazon-cloudwatch-agent.rpm \
&&  sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-config-wizard # Cloudwatch agent Wizard

# edit config file
sudo vim /opt/aws/amazon-cloudwatch-agent/bin/config.json

# RUN
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/bin/config.json -s

# required for CloudWatch Agent
sudo mkdir /usr/share/collectd && sudo touch /usr/share/collectd/types.db

# check status
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -m ec2 -a status

# https://s3.region.amazonaws.com/amazoncloudwatch-agent-region/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm

