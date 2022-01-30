import {sleep, check, fail} from 'k6';
import http from 'k6/http';


export let options = {
    stages: [
        {duration: '10s', target: 500},
        {duration: '5m', target: 500},
    ],
    thresholds: {
        // http errors should be less than 1%
        http_req_failed: ['rate<=0.05'],
        // 95% of requests should be less than 500ms
        http_req_duration: ['p(90)<=1000', 'p(95)<=1250', 'p(100)<=1500'],
        // 95% of checkes should pass
        checks: ['rate>=0.95']
    }
};
const params = {
    headers: {
        'Content-Type': 'application/json',
    },
};

export default () => {
    const res = http.get("http://localhost:8080");
    if (res.status !== 200) {
        console.info(res.body);
        fail();
    }
    sleep(1);
};
