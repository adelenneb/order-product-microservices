# Order Product Microservices
[![CI](https://github.com/adelenneb/order-product-microservices/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/adelenneb/order-product-microservices/actions/workflows/ci.yml)
[![CD](https://github.com/adelenneb/order-product-microservices/actions/workflows/cd.yml/badge.svg)](https://github.com/adelenneb/order-product-microservices/actions/workflows/cd.yml)

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.0-blue?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Kustomize-blueviolet?style=for-the-badge&logo=kubernetes&logoColor=white)

Java 21 / Spring Boot 3.4 microservices demo with centralized configuration, service discovery, API gateway routing, and inter-service calls via OpenFeign.

## Project Purpose
- Services indépendants et containerisés
- Config centralisée (Spring Cloud Config)
- Découverte dynamique (Eureka) et routage (Gateway)
- Communication via OpenFeign
- Déploiement Docker, Compose et Kubernetes

## Architecture
```
                         +-----------------+
                         |  config-server  | 8888
                         +--------+--------+
                                  |
                         loads native config-repo
                                  |
+----------------+       +--------v---------+      +-------------------+
|  api-gateway   | 8080  |  eureka-server   | 8761 |   config-repo      |
|  /products     +------>+  registry & UI   +<-----+  (properties)      |
|  /orders       |       +--------+---------+      +-------------------+
+--------+-------+                |
         |                        |
         | lb://product-service   | lb://order-service
         |                        |
 +-------v-------+        +-------v-------+
 | product-svc   | 8081   | order-svc     | 8082
 | H2 catalog    |        | H2 orders     |
 | Actuator      |        | Feign->product|
 +---------------+        +---------------+
```

## Run with Docker Compose
- Start: `docker-compose up -d --build`
- Stop: `docker-compose down`
- Ports: gateway 8080, product 8081, order 8082, eureka 8761, config 8888.

## Test via API Gateway
- Produits : `curl http://localhost:8080/products`
- Créer une commande :  
  `curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"productId":1,"quantity":2}'`
- Lire une commande : `curl http://localhost:8080/orders/1`

## Service Discovery
- Eureka UI : http://localhost:8761
- Services : `lb://product-service`, `lb://order-service`, `lb://api-gateway`

## Config Server
- Config native depuis `config-repo/`
- Exemple : `curl http://localhost:8888/product-service/default`

## Kubernetes (kustomize)
- Appliquer : `kubectl apply -k k8s/base`
- Services : ClusterIP (sauf api-gateway en NodePort 30080)
- Probes : `/actuator/health`

## Local sans Docker
```
mvn clean package
mvn -pl services/config-server spring-boot:run -Dspring-boot.run.profiles=native
mvn -pl services/eureka-server spring-boot:run
mvn -pl services/product-service spring-boot:run
mvn -pl services/order-service spring-boot:run
mvn -pl services/api-gateway spring-boot:run
```

## CI/CD (GitHub Actions)
- CI (`ci.yml`) : `mvn -B verify` sur chaque push/PR.
- CD (`cd.yml`) : build & push Docker (`latest` + SHA) vers Docker Hub, puis déploiement optionnel vers Kubernetes si les secrets sont présents.
- Secrets : `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `KUBE_CONFIG` (base64 du kubeconfig), `K8S_NAMESPACE`.
- Déclenchement : push sur `main/master` ou manuel via “Run workflow”.
