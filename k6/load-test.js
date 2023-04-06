import { apiUrl, randomUser, signUpUser, signInUser, userProfile } from './common.js';

export const options = {
    stages: [
        { duration: '1m', target: 200 }, // simulate ramp-up of traffic from 1 to 200 users over 1 minute.
        { duration: '1m', target: 200 }, // stay at 200 users for 1 minute
        { duration: '1m', target: 500 }, // ramp-up to 500 users over 1 minute
        { duration: '1m', target: 500 }, // stay at 500 users for 1 minute
        { duration: '1m', target: 200 }, // ramp-down to 200 users over 1 minute
        { duration: '1m', target: 200 }, // continue at 200 for additional 1 minute
        { duration: '1m', target: 0 }, // ramp-down to 0 users
    ],
    thresholds: {
        checks: ['rate>0.95'], // the rate of successful checks should be higher than 95%
        http_req_duration: [
            'p(90) < 1000', // 90% of requests must finish within 1s.
            'p(95) < 1200', // 95% of requests must finish within 1.2s.
            'p(99.9) < 1500', // 99.9% of requests must finish within 1.5s.
        ],
        http_req_failed: ['rate<0.05'], // http errors should be less than 5%
    },
};

export function setup() {
    console.log(`Running load tests on ${apiUrl('')}...`);
}

export default function () {
    const user = randomUser();
    signUpUser(user);
    for (let i = 0; i < 5; i++) {
        const token = signInUser(user);
        userProfile(token);
    }
}
