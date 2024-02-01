#!/usr/bin/env bash
set -e

# Remover as linhas relacionadas às variáveis de ambiente do arquivo .bashrc
sudo sed -i '/export DB_URL/d' /home/ec2-user/.bashrc
sudo sed -i '/export DB_USERNAME/d' /home/ec2-user/.bashrc
sudo sed -i '/export DB_PASSWORD/d' /home/ec2-user/.bashrc

# Remover o diretório
sudo rm -rf /home/ec2-user/server

# Encerrar a aplicação Java
sudo pkill -f 'java -jar'
