variable "product" {
  default = "fact"
}

variable "component" {
  default = "api"
}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "subscription" {}


variable "common_tags" {
  type = map(any)
}

variable "tenant_id" {}

variable "ilbIp" {
  default = ""
}

variable "jenkins_AAD_objectId" {
  type        = string
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "managed_identity_object_id" {
  default = ""
}

variable "aks_subscription_id" {
  default = ""
}
