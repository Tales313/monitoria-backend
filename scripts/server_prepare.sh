#!/usr/bin/env bash
sudo rm -rf /home/ec2-user/server

# Recuperar as variáveis do Secrets Manager
secret_value=$(aws secretsmanager get-secret-value --secret-id homolog/db_monitoria --query SecretString --output text)
db_host=$(echo "$secret_value" | jq -r .host)
db_port=$(echo "$secret_value" | jq -r .port)
db_name=$(echo "$secret_value" | jq -r .dbname)

# Criar a URL completa
db_url="jdbc:postgresql://$db_host:$db_port/$db_name"

# Exportar a variável de ambiente
export DB_URL="$db_url"
export DB_USERNAME$(aws secretsmanager get-secret-value --secret-id homolog/db_monitoria --query SecretString --output text | jq -r .username)
export DB_PASSWORD$(aws secretsmanager get-secret-value --secret-id homolog/db_monitoria --query SecretString --output text | jq -r .password)