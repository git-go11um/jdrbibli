#!/bin/bash

# Script pour démarrer tous les microservices JdrBibli (XFCE4)

# Arrêt en cas d'erreur
set -e

# Démarrage des microservices dans des terminaux séparés
xfce4-terminal --hold --title="Auth-Service" --working-directory="$PWD/auth-service" -e "mvn spring-boot:run" &
xfce4-terminal --hold --title="User-Service" --working-directory="$PWD/user-service" -e "mvn spring-boot:run" &
xfce4-terminal --hold --title="Ouvrage-Service" --working-directory="$PWD/ouvrage-service" -e "mvn spring-boot:run" &
xfce4-terminal --hold --title="Gateway" --working-directory="$PWD/gateway" -e "mvn spring-boot:run" &

echo "🚀 Tous les microservices sont en cours de démarrage dans des terminaux séparés..."
