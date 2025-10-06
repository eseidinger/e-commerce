docker build -t localhost:32000/ecommerce-payara6:latest .
docker push localhost:32000/ecommerce-payara6:latest

kubectl delete ns e-commerce-payara6
kubectl create ns e-commerce-payara6

kubectl apply -n e-commerce-payara6 -f k8s/postgres-statefulset.yaml
kubectl wait -n e-commerce-payara6 --for=condition=available --timeout=60s statefulset/postgres
kubectl apply -n e-commerce-payara6 -f k8s/deployment.yaml
