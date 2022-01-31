import {sleep, check, fail} from 'k6';
import http from 'k6/http';


export let options = {
    vus: 1,
    duration: '1s',

}

const params = {
    headers: {
        'Content-Type': 'application/json',
    },
};


const uri = "http://localhost:8888";

export default () => {
    const res = http.get(uri + "/actuator/threaddump");
    if (res.status === 200) {
        console.info("--------------------------------------------------");
        JSON.parse(res.body).threads.forEach(thr => {
            console.info(thr.threadName);
        });
        console.info("--------------------------------------------------");
        console.info(res.body);
    }
    sleep(1);
};
