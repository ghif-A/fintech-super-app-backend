import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m30s', target: 10 },
    { duration: '20s', target: 0 },
  ],
};

const BASE_URL = 'http://localhost:8000';

export default function () {
  const username = `user${__VU}@example.com`;
  const password = 'password123';

  // Sign up
  let signupRes = http.post(
    `${BASE_URL}/auth/signup`,
    JSON.stringify({ username, password, roles: ['user'] }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(signupRes, { 'signup successful': (r) => r.status === 200 });

  sleep(1);

  // Log in
  let loginRes = http.post(
    `${BASE_URL}/auth/signin`,
    JSON.stringify({ username, password }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(loginRes, { 'login successful': (r) => r.status === 200 });
}
