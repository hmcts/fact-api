provider "azurerm" {
  features {}
}

provider "azurerm" {
  features {}
  skip_provider_registration = true
  alias                      = "postgres_network"
  subscription_id            = var.aks_subscription_id
}

locals {
  vault_name = "${var.product}-${var.env}"
  resource_group_name = "${var.product}-${var.env}"
}

data "azurerm_key_vault" "fact_key_vault" {
  name = local.vault_name
  resource_group_name = local.resource_group_name
}

data "azurerm_key_vault_secret" "launch_darkly_sdk_key" {
  name = "launchdarkly-sdk-key"
  key_vault_id = data.azurerm_key_vault.fact_key_vault.id
}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "api-POSTGRES-USER"
  value        = module.fact-database.user_name
  key_vault_id = data.azurerm_key_vault.fact_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "api-POSTGRES-PASS"
  value        = module.fact-database.postgresql_password
  key_vault_id = data.azurerm_key_vault.fact_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "api-POSTGRES-HOST"
  value        = module.fact-database.host_name
  key_vault_id = data.azurerm_key_vault.fact_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "api-POSTGRES-PORT"
  value        = module.fact-database.postgresql_listen_port
  key_vault_id = data.azurerm_key_vault.fact_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "api-POSTGRES-DATABASE"
  value        = module.fact-database.postgresql_database
  key_vault_id = data.azurerm_key_vault.fact_key_vault.id
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

locals {
  vault_name = "${var.product}-${var.env}"
  resource_group_name = "${var.product}-${var.env}"
  db_host_name = "${var.product}-${var.component}-flexible-postgres-db-v15"
  db_name = replace(var.component, "-", "")
  postgresql_user = "${local.db_name}_user"
}

module "postgresql" {
  providers = {
    azurerm.postgres_network = azurerm.postgres_network
  }

  source               = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  name                 = local.db_host_name
  product              = var.product
  component            = var.component
  location             = var.location
  env                  = var.env
  pgsql_admin_username = local.postgresql_user
  pgsql_databases = [
    {
      name : "fact"
    }
  ]
  common_tags   = var.common_tags
  business_area = "cft"
  pgsql_version = "15"

  admin_user_object_id = var.jenkins_AAD_objectId
}
