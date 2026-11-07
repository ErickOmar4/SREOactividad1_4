#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# vps-run.sh - Inicia, detiene y consulta la aplicación en el VPS usando nohup.
#
# Colócalo en la misma carpeta que el .jar (por ejemplo ~/act1-t4/) y ejecuta:
#
#   chmod +x vps-run.sh
#   PORT=8081 ./vps-run.sh start      # inicia en segundo plano
#   ./vps-run.sh status               # ¿está corriendo?
#   ./vps-run.sh logs                 # sigue el log (Ctrl+C para salir)
#   ./vps-run.sh stop                 # detiene SOLO tu proceso (usa el archivo .pid)
#   PORT=8081 ./vps-run.sh restart
#
# Variables opcionales: JAR (default act1-t4.jar), PORT (default 8081),
#                       LOG_FILE (default app.log), PID_FILE (default app.pid)
# ---------------------------------------------------------------------------
set -euo pipefail

cd "$(dirname "$0")"

JAR="${JAR:-act1-t4.jar}"
PORT="${PORT:-8081}"
LOG_FILE="${LOG_FILE:-app.log}"
PID_FILE="${PID_FILE:-app.pid}"

is_running() {
  [[ -f "$PID_FILE" ]] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null
}

start() {
  if is_running; then
    echo "Ya está corriendo (PID $(cat "$PID_FILE"))."
    return 0
  fi
  if [[ ! -f "$JAR" ]]; then
    echo "No se encontró $JAR en $(pwd)" >&2
    exit 1
  fi
  nohup java -jar "$JAR" --server.port="$PORT" > "$LOG_FILE" 2>&1 &
  echo $! > "$PID_FILE"
  echo "Iniciado en el puerto $PORT (PID $(cat "$PID_FILE")). Log: $LOG_FILE"
}

stop() {
  if ! is_running; then
    echo "No está corriendo."
    rm -f "$PID_FILE"
    return 0
  fi
  local pid
  pid="$(cat "$PID_FILE")"
  kill "$pid"
  for _ in $(seq 1 20); do
    kill -0 "$pid" 2>/dev/null || break
    sleep 0.5
  done
  if kill -0 "$pid" 2>/dev/null; then
    echo "No terminó a tiempo; forzando cierre."
    kill -9 "$pid"
  fi
  rm -f "$PID_FILE"
  echo "Detenido (PID $pid)."
}

status() {
  if is_running; then
    echo "Corriendo (PID $(cat "$PID_FILE")). Consulta el puerto con: ss -tlnp | grep $(cat "$PID_FILE")"
  else
    echo "Detenido."
  fi
}

case "${1:-}" in
  start)   start ;;
  stop)    stop ;;
  restart) stop; start ;;
  status)  status ;;
  logs)    tail -f "$LOG_FILE" ;;
  *)
    echo "Uso: [PORT=8081] $0 {start|stop|restart|status|logs}" >&2
    exit 1
    ;;
esac
