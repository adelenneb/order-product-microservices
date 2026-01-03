# Order Product Microservices
[![CI](https://github.com/adelenneb/order-product-microservices/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/adelenneb/order-product-microservices/actions/workflows/ci.yml)
[![CD](https://github.com/adelenneb/order-product-microservices/actions/workflows/cd.yml/badge.svg?branch=main)](https://github.com/adelenneb/order-product-microservices/actions/workflows/cd.yml)



![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.0-blue?style=for-the-badge&logo=spring&logoColor=white)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-informational?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-v2-blue?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Kustomize-blueviolet?style=for-the-badge&logo=kubernetes&logoColor=white)
![Eureka](https://img.shields.io/badge/Service%20Discovery-Eureka-yellow?style=for-the-badge)
![Gateway](https://img.shields.io/badge/API-Gateway-lightgrey?style=for-the-badge)
![Feign](https://img.shields.io/badge/OpenFeign-Inter--Service%20Calls-success?style=for-the-badge)
![H2](https://img.shields.io/badge/Database-H2-lightgrey?style=for-the-badge)
![Actuator](https://img.shields.io/badge/Monitoring-Actuator-red?style=for-the-badge)


A Java 21 / Spring Boot 3.4 / Spring Cloud 2024.0.0 microservices architecture demonstrating centralized configuration, service discovery, API gateway routing, and inter-service communication using OpenFeign.

This project is designed to showcase real-world microservices patterns commonly used in enterprise systems.

  ## Project Purpose

The goal of this project is to demonstrate:

Independent, containerized microservices

Centralized configuration management

Dynamic service discovery and routing

Clean separation of responsibilities

Cloud-ready architecture (Docker & Kubernetes)

 ## Architecture Overview

```
                         +-----------------+
                         |  config-server  | 8888
                         +--------+--------+
                                  |
                        loads native config-repo
                                  |
+----------------+       +--------v---------+      +-------------------+
|  api-gateway   | 8080  |  eureka-server   | 8761 |   config-repo      |
|  routes to LB  +------>+  registry & UI   +<-----+  (properties)      |
|  /products     |       +--------+---------+      +-------------------+
|  /orders       |                |
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

## Run locally with Docker Compose
```
docker-compose build
docker-compose up -d
# Stop
docker-compose down
```
Ports: gateway 8080, product 8081, order 8082, eureka 8761, config 8888.

## Test via API Gateway
List products:
```
curl http://localhost:8080/products
```
Create order (through gateway → order-service → product-service):
```
 curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"productId":1,"quantity":2}'
```
Get order:
```
curl http://localhost:8080/orders/1
```

## Service discovery
- `eureka-server` exposes registry at http://localhost:8761.
- `product-service`, `order-service`, and `api-gateway` register with Eureka and resolve peers by serviceId (`lb://product-service`, `lb://order-service`).

## Config server
- `config-server` (port 8888) serves properties from `config-repo/` (native profile).
- Services import config via `spring.config.import=optional:configserver:${CONFIG_SERVER_URL}` (defaults to http://config-server:8888).
- Example check: `curl http://localhost:8888/product-service/default`.

## Kubernetes (kustomize)
Manifests under `k8s/base` (Deployments + Services, probes on `/actuator/health`):
```
kubectl apply -k k8s/base
```
Services are ClusterIP except `api-gateway` (NodePort 30080 by default). Adjust to LoadBalancer if needed.

## Local (no Docker)
```
mvn clean package
mvn -pl services/config-server spring-boot:run -Dspring-boot.run.profiles=native
mvn -pl services/eureka-server spring-boot:run
mvn -pl services/product-service spring-boot:run
mvn -pl services/order-service spring-boot:run
mvn -pl services/api-gateway spring-boot:run
```

## Additional curl examples
- Health: `curl http://localhost:8080/actuator/health`
- Eureka UI: `open http://localhost:8761` (or your OS equivalent)
- Direct product: `curl http://localhost:8081/api/products/1`
- Direct order: `curl -X POST http://localhost:8082/api/orders -H "Content-Type: application/json" -d '{"productId":2,"quantity":1}'`

## CI/CD (GitHub Actions)
- **CI** (`ci.yml`) : `mvn -B verify` sur chaque push/PR.
- **CD** (`cd.yml`) : build & push des images Docker (tags `latest` + SHA) vers Docker Hub, puis deploiement optionnel vers Kubernetes si les secrets sont fournis.
- Secrets requis : `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `KUBE_CONFIG` (base64 du kubeconfig du cluster), `K8S_NAMESPACE` (ex: `default`).
- Declencher : push sur `main/master` ou "Run workflow" manuel dans l'onglet Actions.
