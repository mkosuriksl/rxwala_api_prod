#!/bin/bash

set -e 

# Creating the tmp directory for aws code deploy
DIRECTORY="/root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp"
if [ ! -d "$DIRECTORY" ]; then
  mkdir -p /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp
fi

# Deleting all the source from the current depoyment execution
rm -rf /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp/*

#Creating the deployment folder file name
cd /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp
deploy_folder=`date +"%d_%m_%Y_%H_%M_%S_%N"`
echo "${deploy_folder}" > deploy_folder_name

#Creating the deployment folder
mkdir /root/aws-code-deploy/rxwala-api/${deploy_folder}




