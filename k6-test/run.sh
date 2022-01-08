echo "----------------------- STARTING K6 ------------------------------"

k6 run --out csv=./results/scripts/summary.csv --console-output=./results/scripts/console.log  ./simple-delivery-get.js &
k6 run --out csv=./results/scripts/summary.csv --console-output=./results/scripts/console.log  ./process-cpu-usage-test.js &

wait

echo "----------------------- FINISHED ------------------------------"
