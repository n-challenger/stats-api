A couple of scripts to perform service testing:
    - load testing
    - traffic simulator (Run `pip install -r requirements.txt` before running this script)

Load testing results on my laptop(MacBook Pro):

```bash
Document Path:          /transactions

Concurrency Level:      10
Time taken for tests:   2.074 seconds
Complete requests:      10000
Failed requests:        0
Requests per second:    4822.47 [#/sec] (mean)
Time per request:       2.074 [ms] (mean)
Time per request:       0.207 [ms] (mean, across all concurrent requests)

Percentage of the requests served within a certain time (ms)
  50%      2
  66%      2
  75%      2
  80%      2
  90%      2
  95%      3
  98%      4
  99%      6
 100%    211 (longest request)
```
