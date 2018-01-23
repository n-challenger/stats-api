import time
import multiprocessing

import requests

STATS_MAX_TIME_SEC = 60

URL = 'http://localhost:8080'


def now_milliseconds():
    return int(time.time()) * 1000


def post(value, get_time=now_milliseconds):
    txn = {
        'amount': float(value),
        'timestamp': get_time(),
    }
    resp = requests.post(URL + '/transactions', json=txn)

    return resp.status_code


def post_outdated(value):
    return post(value, get_time=lambda: now_milliseconds() - (STATS_MAX_TIME_SEC * 1000 - 1))


def get_stats():
    return requests.get(URL + '/statistics').json()


def post_with_delay(value, delay_sec=1):
    time.sleep(delay_sec)
    return post(value)

if __name__ == '__main__':
    pool = multiprocessing.Pool(processes=10)

    count = 1000
    started_at = time.time()
    points = map(float, range(1, count + 1))
    responses = set(pool.map(post, points))
    assert responses == set([200])
    total_time = time.time() - started_at
    print 'Total time: %f, avg time per call: %f ' % (total_time, total_time/count)
    assert get_stats() == dict(count=count, sum=sum(points), min=min(points), max=max(points), avg=sum(points)/len(points))

    responses = set(pool.map(post_outdated, range(count)))
    print responses
    assert responses == set([204])
    print get_stats()
    
    time.sleep(STATS_MAX_TIME_SEC)
    print get_stats()

    # post stats one by one every second
    responses = set(map(post_with_delay, points))
    print responses
    print get_stats()
