provider "azurerm" {
  features {}
}

locals {
  app = "fact-api"
  app_full_name = "${var.product}-${var.component}"
  vault_name = "${local.app_full_name}-${var.env}"
}

resource "azurerm_resource_group" "rg" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = var.location

  tags = var.common_tags
}

module "key_vault" {
  source = "git@github.com:hmcts/cnp-module-key-vault?ref=azurermv2"
  product = local.app_full_name
  env = var.env
  tenant_id = var.tenant_id
  object_id = var.jenkins_AAD_objectId
  resource_group_name = azurerm_resource_group.rg.name
  product_group_object_id = "5d9cd025-a293-4b97-a0e5-6f43efce02c0"
  common_tags = var.common_tags
  create_managed_identity = true
}

data "azurerm_key_vault" "fact_api_key_vault" {
  name = module.key_vault.key_vault_name
  resource_group_name = module.key_vault.key_vault_name
}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "fact-api-POSTGRES-USER"
  value        = module.fact-database.user_name
  key_vault_id = data.azurerm_key_vault.fact_api_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "fact-api-POSTGRES-PASS"
  value        = module.fact-database.postgresql_password
  key_vault_id = data.azurerm_key_vault.fact_api_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "fact-api-POSTGRES-HOST"
  value        = module.fact-database.host_name
  key_vault_id = data.azurerm_key_vault.fact_api_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "fact-api-POSTGRES-PORT"
  value        = module.fact-database.postgresql_listen_port
  key_vault_id = data.azurerm_key_vault.fact_api_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "fact-api-POSTGRES-DATABASE"
  value        = module.fact-database.postgresql_database
  key_vault_id = data.azurerm_key_vault.fact_api_key_vault.id
}

module "fact-database" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product            = var.product
  location           = var.location
  env                = var.env
  postgresql_user    = "fact"
  database_name      = "fact"
  postgresql_version = "11"
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  storage_mb         = "51200"
  common_tags        = var.common_tags
  subscription       = var.subscription
}

resource "azurerm_key_vault_secret" "AZURE_APPINSGHTS_KEY" {
  name         = "AppInsightsInstrumentationKey"
  value        = azurerm_application_insights.appinsights.instrumentation_key
  key_vault_id = data.azurerm_key_vault.fact_api_key_vault.id
}

resource "azurerm_application_insights" "appinsights" {
  name                = "${var.product}-${var.component}-appinsights-${var.env}"
  location            = var.appinsights_location
  resource_group_name = azurerm_resource_group.rg.name
  application_type    = "web"

  tags = var.common_tags

  lifecycle {
    ignore_changes = [
      # Ignore changes to appinsights as otherwise upgrading to the Azure provider 2.x
      # destroys and re-creates this appinsights instance
      application_type,
    ]
  }
}
