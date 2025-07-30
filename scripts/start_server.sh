
#!/bin/bash
set -e
source /root/.bashrc

cd /root/aws-code-deploy/rxwala-api/aws_code_deploy_tmp
deploy_folder=`cat deploy_folder_name`

#creating the symlink of current folder to latest deployment
ln -sfn /root/aws-code-deploy/rxwala-api/${deploy_folder} /root/aws-code-deploy/rxwala-api/current

set +e
#Starting the service
STATUS="$(systemctl is-active rxwala-api)"
if [ "${STATUS}" == "active" ]; then
    echo "Service is already running so restarting the server"  
    systemctl restart rxwala-api 
else 
    echo "Service is not running"
    echo "Starting the service"
    systemctl start rxwala-api
fi

#Validating the service
STATUS="$(systemctl is-active rxwala-api)"
if [ "${STATUS}" == "active" ]; then
    echo "Service started successfully"
else 
    echo " Service not running.... so exiting "  
    exit 1  
fi
