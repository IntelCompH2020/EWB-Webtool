#!/bin/bash

cd frontend
kubectl delete configmap ewb-frontend --namespace=ewb
kubectl create configmap ewb-frontend --from-file=nginx.conf --from-file=config.json --namespace=ewb
kubectl delete service ewb-frontend --namespace=ewb
kubectl delete deployment ewb-frontend --namespace=ewb
kubectl apply -f frontend-deployment.yaml  --namespace=ewb
kubectl apply -f frontend-service.yaml  --namespace=ewb


cd ../backend-apis
kubectl delete configmap ewb-api --namespace=ewb
kubectl create configmap ewb-api --from-file=../config-files/api/config --namespace=ewb
kubectl apply -f logs-pvc.yaml --namespace=ewb
kubectl delete service ewb-backend-apis --namespace=ewb
kubectl delete deployment ewb-backend-apis --namespace=ewb
kubectl apply -f backend-apis-deployment.yaml  --namespace=ewb
kubectl apply -f backend-apis-service.yaml  --namespace=ewb


cd ../proxy
kubectl delete configmap ewb-proxy --namespace=ewb
kubectl create configmap ewb-proxy --from-file=nginx.conf --from-file=ProxyNginx.conf --namespace=ewb
kubectl delete service ewb-proxy --namespace=ewb
kubectl delete deployment ewb-proxy --namespace=ewb
kubectl apply -f proxy-deployment.yaml  --namespace=ewb
kubectl apply -f proxy-service.yaml  --namespace=ewb

cd ..