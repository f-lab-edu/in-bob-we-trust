sudo yum update

sudo yum install dockerd

sudo dockerd > /dev/null 2>&1 & disown

sudo docker run \
  --network=host \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  --user root \
  -v /prom-vol:/prometheus \
  prom/prometheus  > /dev/null 2>&1 & disown