docker build -t localhost:32000/ecommerce-payara5:latest .
docker push localhost:32000/ecommerce-payara5:latest

kubectl delete ns e-commerce-payara5
kubectl create ns e-commerce-payara5

kubectl apply -n e-commerce-payara5 -f k8s/postgres-statefulset.yaml
kubectl wait -n e-commerce-payara5 --for=condition=available --timeout=60s statefulset/postgres
kubectl apply -n e-commerce-payara5 -f k8s/deployment.yaml
