import {sleep, check, fail} from 'k6';
import http from 'k6/http';
import {textSummary} from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';


export let options = {
    vus: 1,
    duration: '10m',
}

const params = {
    headers: {
        'Content-Type': 'application/json',
    },
};

const uri = "http://localhost:8888";

export default () => {
    console.info("--------------------------------------------------");
    const res = http.get(uri + "/actuator/threaddump");
    if (res.status === 200) {
        JSON.parse(res.body).threads.forEach(thr => {
            console.info(thr.threadName);
        })
    }
    sleep(30);
};
