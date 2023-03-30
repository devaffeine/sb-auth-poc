import { check } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import http from 'k6/http';

export const options = {
    vus: 10,
    duration: '1m0s',
};

export function setup() {
    console.log('Running tests against', baseUrl);
}

export default function () {
    const user = {
        username: "user-" + randomString(10),
        password: randomString(10),
        name: "User " + randomString(10),
    };
    const userJson = JSON.stringify(user);

    const res = http.post(url('/sign-up'), userJson, jsonParams());
    console.log('Sign up to', url('/sign-up'), 'jsonParams()', jsonParams());
    check(res, {
        'sign-up has status 201': (r) => r.status === 201,
        'sign-up hast token': (r) => r.body.includes('token'),
    });

    for (let i = 0; i < 2; i++) {
        const resp = http.post(url('/sign-in'), userJson, jsonParams());
        check(res, {
            'sign-in has status 200': (r) => r.status === 200,
            'sign-in hast token': (r) => r.body.includes('token'),
        });

        const jsonResp = resp.json();
        http.get(url('/me'), jsonParams({
            'Authorization': 'Bearer ' + jsonResp['token'],
        }));
    }
}

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const url = (endpoint) => `${baseUrl}${endpoint}`;

const jsonParams = (headers = {}) => {
    const headersMap = {
        'Content-Type': 'application/json',
    };
    Object.assign(headersMap, headers);
    return {
        headers: headersMap,
    };
};
