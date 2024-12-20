targetScope = 'resourceGroup'

// ------------------
//    PARAMETERS
// ------------------

@description('The location where the resources will be created.')
param location string = resourceGroup().location

@description('The tags to be assigned to the created resources.')
param tags object = {}

@description('The name of the container apps environment.')
param containerAppsEnvironmentName string

@description('The name of the container for the service. The name is use as Dapr App ID.')
param containerName string

// Container Registry & Image
@description('The name of the container registry.')
param containerRegistryName string

@secure()
@description('The username for the container registry to be able to pull images from it.')
param containerRegistryUsername string

@secure()
@description('The password secret reference for the container registry to be able to pull images from it.')
param containerRegistryPassword string

@description('The image for the service.')
param containerImage string

@description('The target and dapr port for the service.')
param portNumber int

// Container Resources
@description('The CPU allocation for the service.')
param cpu string

@description('The Memory allocation for the service.')
param memory string

@description('The minimum instance for the service.')
param minInstance int

@description('The maximum instance for the service.')
param maxInstance int

// Key Vault Secrets
@secure()
@description('The key vault url for Spring Data Mongo DB URI.')
param springDataMongoDBURIKeyVaultUrl string

@secure()
@description('The key vault url for Spring Data Mongo DB name.')
param springDataConfigDBNameKeyVaultUrl string

@secure()
@description('The key vault url for Spring Data Mongo DB name.')
param springDataContactHistoryDBNameKeyVaultUrl string

@secure()
@description('The key vault url for Azure Email Connection.')
param azureEmailConnectionKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Contact History URI.')
param eventGridContactHistoryURIKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Contact History access key.')
param eventGridContactHistoryAccessKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Notifications URI.')
param eventGridNotificationsURIKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Notifications access key.')
param eventGridNotificationsAccessKeyVaultUrl string

// Managed Identities
@secure()
@description('The resource ID of the user assigned managed identity for accessing key vault.')
param keyVaultUserAssignedId string

@secure()
@description('The resource ID of the user assigned managed identity for accessing event grid.')
param eventGridUserAssignedId string

@secure()
@description('The resource ID of the user assigned managed identity for accessing storage queues.')
param storageQueueUserAssignedId string


// Deploy Flags
@description('Deploy Flag for config container app.')
param configDeployFlag bool

@description('Deploy Flag for email container app.')
param emailDeployFlag bool

@description('Deploy Flag for event processor container app.')
param eventProcessorDeployFlag bool

@description('Deploy Flag for contact history container app.')
param contactHistoryDeployFlag bool

// ------------------
// RESOURCES
// ------------------

// Azure Container Apps environment
resource containerAppEnvironment 'Microsoft.App/managedEnvironments@2024-03-01' existing = {
  name: containerAppsEnvironmentName
}

// Module for Config ContainerApp
module configContainerApp 'modules/config.bicep' = if(configDeployFlag) {
  name: 'configContainerApp--${uniqueString(resourceGroup().id)}'
  params: {
    location: location
    tags: tags
    containerAppsEnvironmentId: containerAppEnvironment.id
    containerImage: containerImage
    containerName: containerName
    containerRegistryName: containerRegistryName
    containerRegistryPassword: containerRegistryPassword
    containerRegistryUsername: containerRegistryUsername
    cpu: cpu
    keyVaultUserAssignedId: keyVaultUserAssignedId
    maxInstance: maxInstance
    memory: memory
    minInstance: minInstance
    portNumber: portNumber
    springDataConfigDBNameKeyVaultUrl: springDataConfigDBNameKeyVaultUrl
    springDataMongoDBURIKeyVaultUrl: springDataMongoDBURIKeyVaultUrl
  }
}

// Module for Email ContainerApp
module emailContainerApp 'modules/email.bicep' = if(emailDeployFlag) {
  name: 'emailContainerApp--${uniqueString(resourceGroup().id)}'
  params: {
    location: location
    tags: tags
    containerAppsEnvironmentId: containerAppEnvironment.id
    containerImage: containerImage
    containerName: containerName
    containerRegistryName: containerRegistryName
    containerRegistryPassword: containerRegistryPassword
    containerRegistryUsername: containerRegistryUsername
    cpu: cpu
    keyVaultUserAssignedId: keyVaultUserAssignedId
    maxInstance: maxInstance
    memory: memory
    minInstance: minInstance
    portNumber: portNumber
    azureEmailConnectionKeyVaultUrl: azureEmailConnectionKeyVaultUrl
    eventGridContactHistoryURIKeyVaultUrl: eventGridContactHistoryURIKeyVaultUrl
    eventGridContactHistoryAccessKeyVaultUrl: eventGridContactHistoryAccessKeyVaultUrl
    storageQueueUserAssignedId: storageQueueUserAssignedId
  }
}

// Module for Event Processor ContainerApp
module eventProcessorContainerApp 'modules/event-processor.bicep' = if(eventProcessorDeployFlag) {
  name: 'eventProcessorContainerApp--${uniqueString(resourceGroup().id)}'
  params: {
    location: location
    tags: tags
    containerAppsEnvironmentId: containerAppEnvironment.id
    containerImage: containerImage
    containerName: containerName
    containerRegistryName: containerRegistryName
    containerRegistryPassword: containerRegistryPassword
    containerRegistryUsername: containerRegistryUsername
    cpu: cpu
    keyVaultUserAssignedId: keyVaultUserAssignedId
    maxInstance: maxInstance
    memory: memory
    minInstance: minInstance
    portNumber: portNumber
    eventGridContactHistoryURIKeyVaultUrl: eventGridContactHistoryURIKeyVaultUrl
    eventGridContactHistoryAccessKeyVaultUrl: eventGridContactHistoryAccessKeyVaultUrl
    eventGridNotificationsAccessKeyVaultUrl: eventGridNotificationsAccessKeyVaultUrl
    eventGridNotificationsURIKeyVaultUrl: eventGridNotificationsURIKeyVaultUrl
    eventGridUserAssignedId: eventGridUserAssignedId
  }
}

// Module for Contact History ContainerApp
module contactHistoryContainerApp 'modules/contact-history.bicep' = if(contactHistoryDeployFlag) {
  name: 'contactHistoryContainerApp--${uniqueString(resourceGroup().id)}'
  params: {
    location: location
    tags: tags
    containerAppsEnvironmentId: containerAppEnvironment.id
    containerImage: containerImage
    containerName: containerName
    containerRegistryName: containerRegistryName
    containerRegistryPassword: containerRegistryPassword
    containerRegistryUsername: containerRegistryUsername
    cpu: cpu
    keyVaultUserAssignedId: keyVaultUserAssignedId
    storageQueueUserAssignedId: storageQueueUserAssignedId
    maxInstance: maxInstance
    memory: memory
    minInstance: minInstance
    portNumber: portNumber
    springDataContactHistoryDBNameKeyVaultUrl: springDataContactHistoryDBNameKeyVaultUrl
    springDataMongoDBURIKeyVaultUrl: springDataMongoDBURIKeyVaultUrl
  }
}

// ------------------
// OUTPUTS
// ------------------

@description('The name of the container app for the config service.')
output configContainerAppName string = configContainerApp.outputs.containerAppName

@description('The FQDN of the config service.')
output configContainerAppFQDN string = configContainerApp.outputs.containerAppFQDN
