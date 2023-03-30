import http from 'k6/http';
import { sleep } from 'k6';

export default function () {
    const user = {
        username: "myuser-" + new Date().getTime(),
        password: "somepass",
        name: "My User " + new Date().getTime(),
    }
    let url = 'http://localhost:8080/sign-up';
    let payload = JSON.stringify(user);

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.post(url, payload, params);
    //sleep(Math.random() * 3);

    for(let i = 0; i < 2; i++) {
        url = 'http://localhost:8080/sign-in';
        let resp = http.post(url, payload, params);
        //sleep(Math.random() * 3);

        let jsonResp = resp.json();
        params.headers['Authorization'] = 'Bearer ' + jsonResp['token'];
        url = 'http://localhost:8080/me'
        http.get(url, params);
        //sleep(Math.random() * 3);
    }
}