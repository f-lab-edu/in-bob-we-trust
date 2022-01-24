sudo yum update

sudo yum install dockerd

sudo dockerd > /dev/null 2>&1 & disown

sudo docker run \
  -p "9090:9090" \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus  > /dev/null 2>&1 & disown
