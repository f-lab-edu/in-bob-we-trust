#!/bin/bash
cd /home/ec2-user

# install git
yum -y install git

# install docker
sudo amazon-linux-extras install docker && sudo service docker start && sudo usermod -a -G docker ec2-user

# K6 Maximizze machine network utilization
sudo sysctl -w net.ipv4.ip_local_port_range="1024 65535"
sduo sysctl -w net.ipv4.tcp_tw_reuse=1
sudo sysctl -w net.ipv4.tcp_timestamps=1
sudo ulimit -n 250000

# install docker-compose
sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose version

# get in-bob-resources
cd /home/ec2-user

git clone https://github.com/JooHyukKim/in-bob-we-trust.git

# run dashboard
git clone https://github.com/grafana/k6
cd /home/ec2-user/k6 && \
docker-compose up -d \
    influxdb \
    grafana

# docker run
cd /home/ec2-user/k6 \
&& docker-compose run -v \
    /home/ec2-user/in-bob-we-trust/k6-test/scale-out-traffic:/scripts \
    k6 run /scripts/script1.js

# sudo vim /home/ec2-user/in-bob-we-trust/k6-test/scale-out-traffic/script1.js
# -----------------------------------------------------
# -----------------------------------------------------
# -----------------------------------------------------

#sudo vim /home/ec2-user/in-bob-we-trust/k6-test/scale-out-traffic/script1.js
# ver1. set up cloud-watch agent
wget https://s3.ap-northeast-2.amazonaws.com/amazoncloudwatch-agent-ap-northeast-2/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm \
&& sudo rpm -U ./amazon-cloudwatch-agent.rpm
#
#
#c5.xlarge 4vCPU, 8 GiB mem
#i3.2xlarge 8vCPU, 61 GiB mem
#t3.medium 2vCPU 4 GiB eme
