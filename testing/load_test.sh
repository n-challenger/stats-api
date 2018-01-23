
URL="http://localhost:8080/transactions"

BODY="/tmp/sample.json"

MAX_COUNT=10000

CONCURRENCY=10

echo "{\"amount\":12.3,\"timestamp\":`date +%s`000}" > $BODY

ab -n$MAX_COUNT -c$CONCURRENCY -p "$BODY" -T 'application/json' "$URL"

rm $BODY
