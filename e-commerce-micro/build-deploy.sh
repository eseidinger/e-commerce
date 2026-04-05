docker build -t localhost:32000/ecommerce-micro:latest .
docker push localhost:32000/ecommerce-micro:latest

microk8s.kubectl delete ns e-commerce-micro
microk8s.kubectl create ns e-commerce-micro

microk8s.kubectl apply -n e-commerce-micro -f k8s/postgres-statefulset.yaml
microk8s.kubectl wait -n e-commerce-micro --for=condition=available --timeout=60s statefulset/postgres
microk8s.kubectl apply -n e-commerce-micro -f k8s/deployment.yaml
