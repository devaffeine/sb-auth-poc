#!/bin/bash

CLUSTER_NAME=global-auth-cockroachdb-cluster
k3d cluster delete ${CLUSTER_NAME}
sudo rm -rf vol/*