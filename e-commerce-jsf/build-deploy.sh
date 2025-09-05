docker build -t localhost:32000/ecommerce-jsf:latest .
docker push localhost:32000/ecommerce-jsf:latest

kubectl delete ns e-commerce-jsf
kubectl create ns e-commerce-jsf

kubectl apply -n e-commerce-jsf -f k8s/postgres-statefulset.yaml
kubectl wait -n e-commerce-jsf --for=condition=available --timeout=60s statefulset/postgres
kubectl apply -n e-commerce-jsf -f k8s/deployment.yaml
