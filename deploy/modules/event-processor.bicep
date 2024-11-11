targetScope = 'resourceGroup'

// ------------------
//    PARAMETERS
// ------------------

@description('The location where the resources will be created.')
param location string = resourceGroup().location

@description('Optional. The tags to be assigned to the created resources.')
param tags object = {}

@description('The resource Id of the container apps environment.')
param containerAppsEnvironmentId string

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

// User Assigned Identities
@secure()
@description('The resource ID of the user assigned managed identity for accessing key vault.')
param keyVaultUserAssignedId string

@secure()
@description('The resource ID of the user assigned managed identity for accessing event grid.')
param eventGridUserAssignedId string

// Key Vault Secrets
@secure()
@description('The key vault url for Azure Event Grid - Contact History URI.')
param eventGridContactHisotryURIKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Contact History access key.')
param eventGridContactHistoryAccessKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Notifications URI.')
param eventGridNotificationsURIKeyVaultUrl string

@secure()
@description('The key vault url for Azure Event Grid - Notifications access key.')
param eventGridNotificationsAccessKeyVaultUrl string

//@secure()
//@description('The Application Insights Instrumentation.')
//param appInsightsInstrumentationKey string

// ------------------
// RESOURCES
// ------------------

resource containerApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: containerName
  location: location
  tags: tags
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${keyVaultUserAssignedId}': {}
      '${eventGridUserAssignedId}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironmentId
    configuration: {
      activeRevisionsMode: 'single'
      ingress: {
        external: true
        targetPort: portNumber
      }
      dapr: {
        enabled: true
        appId: containerName
        appProtocol: 'http'
        appPort: portNumber
        logLevel: 'info'
        enableApiLogging: true
      }
      secrets: [
        {
          name: 'ghcr-password'
          value: containerRegistryPassword
        }
        {
          identity: keyVaultUserAssignedId
          keyVaultUrl: eventGridContactHistoryAccessKeyVaultUrl
          name: 'aeg-contact-history-key'
        }
        {
          identity: keyVaultUserAssignedId
          keyVaultUrl: eventGridContactHisotryURIKeyVaultUrl
          name: 'aeg-contact-history-url'
        }
        {
          identity: keyVaultUserAssignedId
          keyVaultUrl: eventGridNotificationsAccessKeyVaultUrl
          name: 'aeg-notifications-key'
        }
        {
          identity: keyVaultUserAssignedId
          keyVaultUrl: eventGridNotificationsURIKeyVaultUrl
          name: 'aeg-notifications-url'
        }
      ]
      registries: !empty(containerRegistryName)
        ? [
            {
              server: containerRegistryName
              username: containerRegistryUsername
              passwordSecretRef: 'ghcr-password'
              //          identity: containerRegistryUserAssignedIdentityId
            }
          ]
        : []
    }
    template: {
      containers: [
        {
          name: containerName
          image: containerImage
          resources: {
            cpu: json(cpu)
            memory: memory
          }
          env: [
            {
              name: 'app.client.contact-history.access-key'
              secretRef: 'aeg-contact-history-key'
            }
            {
              name: 'app.client.contact-history.url'
              secretRef: 'aeg-contact-history-url'
            }
            {
              name: 'app.client.notifications.access-key'
              secretRef: 'aeg-notifications-key'
            }
            {
              name: 'app.client.notifications.url'
              secretRef: 'aeg-notifications-url'
            }
          ]
        }
      ]
      scale: {
        minReplicas: minInstance
        maxReplicas: maxInstance
      }
    }
  }
}

// ------------------
// OUTPUTS
// ------------------

@description('The name of the container app for the service.')
output containerAppName string = containerApp.name

@description('The FQDN of the service.')
output containerAppFQDN string = containerApp.properties.configuration.ingress.fqdn
