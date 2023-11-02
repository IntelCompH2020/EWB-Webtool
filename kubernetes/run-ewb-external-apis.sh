#!/bin/bash

kubectl delete configmap ewb-external-api --namespace=ewb
kubectl create configmap ewb-external-api --from-file=config-files/external-apis/config.cf --namespace=ewb


kubectl delete service ewb-external-backend-apis --namespace=ewb
kubectl delete deployment ewb-external-backend-apis --namespace=ewb

kubectl apply -f ewb-external/external-backend-apis-deployment.yaml  --namespace=ewb
kubectl apply -f ewb-external/external-backend-apis-service.yaml  --namespace=ewb