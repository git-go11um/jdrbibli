#!/bin/sh
# wait-for.sh
set -e

host="$1"
port="$2"
shift 2
cmd="$@"

until nc -z "$host" "$port"; do
  echo "⏳ Waiting for $host:$port to be ready..."
  sleep 2
done

>&2 echo "✅ $host:$port is up — executing command"
exec $cmd
