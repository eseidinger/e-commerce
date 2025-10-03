docker build -t localhost:32000/ecommerce-web:latest .
docker push localhost:32000/ecommerce-web:latest

kubectl delete ns e-commerce-web
kubectl create ns e-commerce-web

kubectl apply -n e-commerce-web -f k8s/postgres-statefulset.yaml
kubectl wait -n e-commerce-web --for=condition=available --timeout=60s statefulset/postgres
kubectl apply -n e-commerce-web -f k8s/deployment.yaml
