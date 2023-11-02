#!/bin/bash

kubectl apply -f postgres-configmap.yaml --namespace=ewb
kubectl delete service postgres --namespace=ewb
kubectl delete deployment postgres --namespace=ewb
#kubectl delete pvc postgres-volume-claim --namespace=ewb
kubectl apply -f postgres-pvc.yaml --namespace=ewb
kubectl apply -f postgres-deployment.yaml --namespace=ewb
kubectl apply -f postgres-service.yaml --namespace=ewb
