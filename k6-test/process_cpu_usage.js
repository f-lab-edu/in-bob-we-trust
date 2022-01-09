import { sleep, check, fail } from 'k6';
import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { Trend } from 'k6/metrics';


export let options = {
  vus: 1,
  duration: '1s',
  thresholds: {
    // 100% of checkes should pass
    checks: ['rate==1'],
    // !!!IMPORTANT 100% of process_cpu_usage should be less than 0.2 (20%)
    process_cpu_usage: ['p(100)<=0.2']
  }
};

const customTrend = new Trend('process_cpu_usage');
const time = '1m10s';


export default () => {

  const res = http.get(`http://localhost:9090/api/v1/query?query=process_cpu_usage{job="delivery-info-service"}[${time}]&step=1`);

  const body = JSON.parse(res.body);
  const values = body.data.result[0].values;

  if (values.length <= 10) { // cpu.usage 데이터가 10개보다면적으면 비정상적인것으로 판단하기
    const errorMessage = `defined expected timeInSeconds is ${timeInSeconds} ..... but actual is ${values.length}`;
    console.error(errorMessage);
    fail(errorMessage);
  }

  values.forEach(val => 기
    console.info(`process.cpu.usage     : ${val[1]}`);
  customTrend.add(Number(val[1]));
});

sleep(0.1);
};

export function handleSummary(data) {
  console.log('Preparing the end-of-test summary...');
  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }), // Show the text summary to stdout...
    './results/simple-delivery-get-summary.json': JSON.stringify(data), // and a JSON with all the details...
  };
}

// interface aMetric {
//   name: string;
//   description: string;
//   baseUnit: string,
//   measurements: aMeasurement[],
//   availableTags: any[]
// };

// interface aMeasurement {
//   statistic: string,
//   value: number
// }
