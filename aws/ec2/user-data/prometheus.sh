#!/bin/bash
sudo yum update

# install docker
sudo amazon-linux-extras install docker && sudo service docker start && sudo usermod -a -G docker ec2-user

sudo dockerd > /dev/null 2>&1 & disown

cat << EOF > /home/ec2-user/prometheus.yml
# Sample Prometheus config
# This assumes that your Prometheus instance can access this application on localhost:8080

global:
  scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

scrape_configs:
  - job_name: 'delivery-relay-service-1'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8090" ]

  - job_name: 'delivery-relay-service-2'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8090" ]

  - job_name: 'delivery-relay-service-3'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8090" ]

  - job_name: 'delivery-relay-service-4'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8090" ]

  - job_name: 'delivery-relay-service-5'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8090" ]

  - job_name: 'delivery-info-service-1'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-2'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-3'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-4'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-5'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-6'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-7'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-8'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-9'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]

  - job_name: 'delivery-info-service-10'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets : ["ec2-some-ip.ap-northeast-2.compute.amazonaws.com:8888"]
EOF


sudo docker run \
  --network=host \
  -v /home/ec2-user/prometheus.yml:/etc/prometheus/prometheus.yml \
  --user root \
  -v /prom-vol:/prometheus \
  prom/prometheus  > /dev/null 2>&1 & disown