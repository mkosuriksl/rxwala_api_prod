
#!/bin/bash
set -e 

# Creating the directory of the current deployment
source /root/.bashrc


cd /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp/

deploy_folder=`cat deploy_folder_name`
# Copying the current deployment folder to the deployment specific folder
ls -ltr /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp
cp -r /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp/* /root/aws-code-deploy/rxwala-api/${deploy_folder}
ls -ltr /root/aws-code-deploy/rxwala-api/${deploy_folder}

#copying the systemd directory file for starting the springboot service
cp /root/aws-code-deploy/rxwala-api/${deploy_folder}/scripts/rxwala-api.service /usr/lib/systemd/system/
systemctl daemon-reload

# Building the jar file
cd /root/aws-code-deploy/rxwala-api/${deploy_folder}
mvn package


