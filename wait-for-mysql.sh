#!/bin/bash

echo "Aguardando MySQL iniciar..."

while ! nc -z localhost 3306; do
  sleep 2
  echo "Esperando pelo MySQL na porta 3306..."
done

echo "MySQL está ativo. Iniciando aplicação..."
exec "$@"