#!/bin/bash

### K3D cluster
CLUSTER_NAME=global-auth-cockroachdb-cluster
k3d cluster create $CLUSTER_NAME \
    -v $(pwd)/vol:/var/lib/rancher/k3s/storage@all \
    -a 10 -s 1 \
    -p "8090:8090@loadbalancer" \
    --registry-create $CLUSTER_NAME-registry.local:0.0.0.0:5000

###################### Tools ###########################

### Kubernetes Dashboard
# Docs: https://github.com/kubernetes/dashboard/
GITHUB_URL=https://github.com/kubernetes/dashboard/releases
VERSION_KUBE_DASHBOARD=$(curl -w '%{url_effective}' -I -L -s -S ${GITHUB_URL}/latest -o /dev/null | sed -e 's|.*/||')
kubectl create -f https://raw.githubusercontent.com/kubernetes/dashboard/$VERSION_KUBE_DASHBOARD/aio/deploy/recommended.yaml
kubectl apply -f cfg/dashboard/admin.sec.yml

### Prometheus Monitoring
# Docs:
#   https://github.com/prometheus-community/helm-charts
#   https://blog.devops.dev/monitoring-a-spring-boot-application-in-kubernetes-with-prometheus-a2d4ec7f9922
# helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
# helm repo update
helm install -f cfg/prometheus/values.yml prometheus prometheus-community/kube-prometheus-stack

### Splunk
# Docs: https://operatorhub.io/operator/splunk
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.24.0/install.sh | bash -s v0.24.0
kubectl create -f https://operatorhub.io/install/splunk.yaml
kubectl create -f cfg/splunk/s1.splunk.yml

### Cert Manager
# Docs: https://cert-manager.io/
# Helm Repo:
#     helm repo add jetstack https://charts.jetstack.io
helm install cert-manager jetstack/cert-manager --namespace cert-manager --create-namespace --set installCRDs=true

###################### DBs ###########################

### Cockroach DB
# Docs: https://www.cockroachlabs.com/
# Helm Repo:
#    helm repo add cockroachdb https://charts.cockroachdb.com/
#    helm repo update
helm install myroach --values cfg/cockroachdb/values.yml cockroachdb/cockroachdb

# Exposing Services:
# Prometeus:
#     kubectl port-forward service/community-prometheus 8081:80
# Splunk:
#     kubectl get secret splunk-default-secret -o go-template='{{range $k,$v := .data}}{{printf "%s: " $k}}{{if not $v}}{{$v}}{{else}}{{$v | base64decode}}{{end}}{{"\n"}}{{end}}'
#     kubectl port-forward splunk-s1-standalone-0 8000
# Redis
#     kubectl port-forward svc/rfs-redisfailover 26379
#     kubectl run --rm -it myshell --image=redis -- redis-cli -h rfs-redisfailover.default.svc.cluster.local -p 26379
# Cassandra:
#      kubectl exec -it demo-dc1-default-sts-0 -n k8ssandra-operator -c cassandra -- nodetool -u $CASS_USERNAME -pw $CASS_PASSWORD status
# ElasticSearch:
#     kubectl get secret quickstart-es-elastic-user -o go-template='{{.data.elastic | base64decode}}'
#     kubectl port-forward service/quickstart-es-http 9200

kubectl -n kubernetes-dashboard create token admin-user
# http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/service?namespace=default

kubectl apply -f cfg/auth-app/auth-app.deploy.yml