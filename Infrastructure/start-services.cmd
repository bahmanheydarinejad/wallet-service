@echo off
REM Create volumes and network if not exist
docker volume inspect artifacts >nul 2>&1 || docker volume create artifacts
docker volume inspect maven-cache >nul 2>&1 || docker volume create maven-cache
docker network inspect app-net >nul 2>&1 || docker network create app-net

echo Starting MySQL container...
docker run -d --name mysql ^
  --hostname mysql ^
  -e MYSQL_DATABASE=wallet-service ^
  -e MYSQL_ROOT_PASSWORD=mysql_wallet_123 ^
  -p 3306:3306 -p 33060:33060 ^
  --network app-net ^
  mysql:9

echo Waiting 15 seconds for MySQL to initialize...
timeout /t 15 /nobreak >nul

echo Starting jar-maker-22 container...
docker run -d --name maven-jdk22 ^
  -e GIT_REPO_URL=https://github.com/bahmanheydarinejad/sample-wallet-service.git ^
  -e GIT_BRANCH=main ^
  -v artifacts:/artifacts ^
  -v maven-cache:/root/.m2 ^
  --network app-net ^
  bahmanheydarinejad/jar-maker-22:v0.0.1

echo Waiting 15 seconds for jar-maker-22 to complete...
timeout /t 15 /nobreak >nul

echo Starting wallet-service container...
docker run -d --name wallet-service ^
  --hostname wallet-service ^
  -p 80:8080 ^
  --network app-net ^
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/wallet-service ^
  -e SPRING_DATASOURCE_USERNAME=root ^
  -e SPRING_DATASOURCE_PASSWORD=mysql_wallet_123 ^
  -e SERVER_PORT=8080 ^
  -e LOGGING_LEVEL_ROOT=info ^
  -e SPRING_PROFILES_ACTIVE=prod ^
  -v artifacts:/artifacts ^
  bahmanheydarinejad/wallet-service

echo All containers started.
