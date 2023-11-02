#!/bin/bash
kubectl delete deployment zoo-service --namespace=ewb
kubectl delete deployment solr-service --namespace=ewb

kubectl apply -f ewb-external/solr-deployment.yaml  --namespace=ewb
kubectl apply -f ewb-external/solr-service.yaml  --namespace=ewb

kubectl apply -f ewb-external/zoo-deployment.yaml  --namespace=ewb
kubectl apply -f ewb-external/zoo-service.yaml  --namespace=ewb