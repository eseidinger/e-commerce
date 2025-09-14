docker build -t localhost:32000/ecommerce-micro:latest .
docker push localhost:32000/ecommerce-micro:latest

kubectl delete ns e-commerce-micro
kubectl create ns e-commerce-micro

kubectl apply -n e-commerce-micro -f k8s/postgres-statefulset.yaml
kubectl wait -n e-commerce-micro --for=condition=available --timeout=60s statefulset/postgres
kubectl apply -n e-commerce-micro -f k8s/deployment.yaml
