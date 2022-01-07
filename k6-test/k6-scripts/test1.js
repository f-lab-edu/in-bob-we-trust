import { sleep, check, fail } from 'k6';
import http from 'k6/http';

export const options = {
  scenarios: {
    process_cpu_usage_is_less_than_70_percent: {
      // some arbitrary scenario name
      executor: 'constant-vus',
      gracefulStop: '10s', // do not wait for iterations to finish in the end
      vus: 1,
      duration: '10s',
      exec: 'process_cpu_usage_is_less_than_70_percent', // the function this scenario will execute
    },
    findAllDeliveries_is_200: {
      executor: 'ramping-arrival-rate',
      gracefulStop: '10s', // do not wait for iterations to finish in the end
      // startTime: '30s', // the ramping API test starts a little later
      startRate: 50,
      timeUnit: '1s', // we start at 50 iterations per second
      stages: [
        // { duration: '30s', target: 100 }, // below normal load
        // { duration: '1m', target: 100 },
        // { duration: '1m', target: 1000 },
        // { duration: '1m', target: 2000 },
        // { duration: '1m', target: 1000 },
        // { duration: '30s', target: 100 }, // below normal load
        { duration: '10s', target: 1000 }, // below normal load
      ],
      preAllocatedVUs: 50, // how large the initial pool of VUs would be
      maxVUs: 100, // if the preAllocatedVUs are not enough, we can initialize more
      exec: 'findAllDeliveries_is_200', // same function as the scenario above, but with different env vars
    },
  },
  discardResponseBodies: true,
  thresholds: {
    // process_cpu_usage_is_less_than_70_percent scenario
    'http_req_failed{scenario:process_cpu_usage_is_less_than_70_percent}': ['rate<0.01'],
    'http_req_duration{scenario:process_cpu_usage_is_less_than_70_percent}': ['rate==1.0'],
    'checks{scenario:process_cpu_usage_is_less_than_70_percent}': ['rate<0.01'],

    // findAllDeliveries_is_200 
    'http_req_failed{scenario:findAllDeliveries_is_200}': ['rate<=0.01'], // http errors should be less than 1%
    'http_req_duration{scenario:findAllDeliveries_is_200}': ['p(90)<=500', 'p(95)<=750', 'p(100)<=1000'], // 95% of requests should be less than 500ms
    'checks{scenario:findAllDeliveries_is_200}': ['rate>=0.99'] // 99% of checkes should pass
  },
};

export function process_cpu_usage_is_less_than_70_percent() {
  // given
  const uri = "http://localhost:8080/actuator/metrics/process.cpu.usage";
  const expectedMax = 0.7;

  // when
  const res = http.get(uri);

  // then
  check(res, {
    'Status is 200     ': () => {
      if (res.status === 404) fail('CONNECTION_REFUSED 404');
      return res.status === 200;
    },
  });

  check(res, {
    'Metric value is     ': () => {
      console.info(new String(res.body).toString());

      const metric = JSON.parse(res.body);
      const value = metricNotNull(metric, res.status);
      return value <= expectedMax;
    }

  });

  sleep(0.5);
};

export function findAllDeliveries_is_200() {
  // given 
  const uri = "http://localhost:8080/api/delivery";

  // when 
  const res = http.get(uri);

  // then
  check(res, {
    'Status is 200     ': () => {
      console.info(JSON.parse(res.body));
      return res.status === 200;
    },
  });

  sleep(0.1);
};

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


export function handleSummary(data) {
  console.log('Preparing the end-of-test summary...');

  // Send the results to some remote server or trigger a hook

  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }), // Show the text summary to stdout...
    'parallel-test-summary.json': JSON.stringify(data), // and a JSON with all the details...
  };
}