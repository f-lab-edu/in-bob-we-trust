import { sleep, check } from 'k6';
import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const uri = "http://localhost:8888/actuator/metrics/process.cpu.usage";
const expectedMax = 0.7;

export let options = {
  vus: 1,
  duration: '60s',
  thresholds: {
    http_req_failed: ['rate<0.01'], // http errors should be less than 1%
    http_req_duration: ['p(100)<=1000'], // 100% of requests should be maximum of 1000ms
    checks: ['rate==1.0'] // 100% of checkes should pass
  }
};

export default () => {
  const res = http.get(uri);

  check(res, {
    'Metric value is     ': () => {
      const metric = JSON.parse(res.body);
      metricNotNull(metric);
      console.info('metric -------' + JSON.stringify(metric));
      return metric.measurements[0].value <= expectedMax;
    }

  });

  sleep(0.5);
};

export function handleSummary(data) {
  console.log('Preparing the end-of-test summary...');

  console.info(new String(data).toString());

  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }), // Show the text summary to stdout...
    './results/process-cpu-usage-test-summary.json': JSON.stringify(data), // and a JSON with all the details...
  };
}

function metricNotNull(metric, status) {
  if (metric === null) {
    throw 'status : ' + status + '  |   metric not found';
  }
  if (metric.measurements === null) {
    throw 'status : ' + status + '  |   metric.measuarements not found';
  }
  if (metric.measurements.length != 1) {
    throw 'status : ' + status + '  |   metric.measurement.length is NOT 1';
  }
  if (metric.measurements[0] === null) {
    throw 'status : ' + status + '  |   metric.measurement[0] is Null';
  }
  return metric.measurements[0].value;
}

