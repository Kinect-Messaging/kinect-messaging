# Deployment Reference
Container Apps Deployment via Bicep and GitHub Actions
## GitHub Workflow Setup
1. Create Azure AD application and Service Principal
```shell
AZURE_TENANT=$(az account show -o tsv --query tenantId)
SUBSCRIPTION_ID=$(az account show -o tsv --query id)

#Uncomment below 2 lines if need to create a new app
#APP_ID=$(az ad app create --display-name dev-github-cicd-app --query appId -otsv)
#az ad sp create --id $APP_ID --query appId -otsv

#Directly assign app id if app already exists
APP_ID=95dc5ece-d32f-487f-b841-7fceddb2c6d7 

OBJECT_ID=$(az ad app show --id $APP_ID --query id -otsv)

CONTAINERAPPS_ENV_ID=$(az containerapp env show -g dev-apps-rg -n development --query id)
```

2. Create Federated Identity credential for Azure AD application
```shell
cat <<EOF > body.json
{
    "name": "dev-github-kinect-config-app-cicd-federated-identity",
    "issuer": "https://token.actions.githubusercontent.com",
    "subject": "repo:Kinect-Messaging/kinect-config-app:environment:dev",
    "description": "GitHub Kinect App Repo",
    "audiences": [
        "api://AzureADTokenExchange"
    ]
}
EOF

az rest --method POST --uri "https://graph.microsoft.com/beta/applications/$OBJECT_ID/federatedIdentityCredentials" --body @body.json
```
3. Assign roles for the Azure AD application to access the subscription
```shell
az role assignment create --assignee $APP_ID --role contributor --scope /subscriptions/$SUBSCRIPTION_ID
az role assignment create --assignee $APP_ID --role 'User Access Administrator' --scope /subscriptions/$SUBSCRIPTION_ID
```

4. Configure GitHub Repository Variables
```shell
# LOCATION: Azure region where resources will be deployed
LOCATION=<location. e.g. eastus>

# RESOURCE_GROUP: Name of the resource group which will be created and resources will be deployed
RESOURCE_GROUP=<resource group name>

# (OPTIONAL)CONTAINER_REGISTRY_NAME: Unique name of the container registry which will be created and where images will be imported
CONTAINER_REGISTRY_NAME=<container registry name>
```

5. Configure GitHub Repository secrets
```shell
# AZURE_SUBSCRIPTION_ID
echo $SUBSCRIPTION_ID
# AZURE_TENANT_ID
echo $AZURE_TENANT
# AZURE_CLIENT_ID
echo $APP_ID
# CONTAINERAPPS_ENV_ID
echo $CONTAINERAPPS_ENV_ID
```

## References
* [Bicep](https://azure.github.io/aca-dotnet-workshop/aca/10-aca-iac-bicep/iac-bicep/) 
* [GitHub Actions](https://azure.github.io/aca-dotnet-workshop/aca/10-aca-iac-bicep/ci-cd-git-action/#__tabbed_1_2)