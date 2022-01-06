import { sleep, check } from 'k6';
import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const uri = "http://localhost:8080/api/delivery";

export let options = {
  vus: 1000,
  thresholds: {
    // http errors should be less than 1%
    http_req_failed: ['rate<=0.01'],
    // 95% of requests should be less than 500ms
    http_req_duration: ['p(90)<=500', 'p(95)<=750', 'p(100)<=1000'],
    // 99% of checkes should pass
    checks: ['rate>=0.99']
  }
};

export default () => {
  const res = http.get(uri);

  check(res, {
    'Status is 200     ': () => res.status === 200,
  });

  sleep(0.1);
};

export function handleSummary(data) {
  console.log('Preparing the end-of-test summary...');

  // Send the results to some remote server or trigger a hook
  const resp = http.post('https://httpbin.test.k6.io/anything', JSON.stringify(data));
  if (resp.status != 200) {
    console.error('Could not send summary, got status ' + resp.status);
  }

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
