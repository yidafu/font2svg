import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  // A number specifying the number of VUs to run concurrently.
  vus: 100,
  // A string specifying the total duration of the test run.
  duration: '3m',
};

// The function that defines VU logic.
//
// See https://grafana.com/docs/k6/latest/examples/get-started-with-k6/ to learn more
// about authoring k6 scripts.
//
export default function() {
  for (let charCode = 0; charCode < 65510; charCode ++) {
    http.get(`http://127.0.0.1:8888/asserts/svg/JinBuTi/${charCode}.svg`, {
      tags: {
        StaticFontSvg: `${charCode}`
      }
    })
  }
}
