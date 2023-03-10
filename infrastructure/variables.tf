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

variable "deployment_namespace" {}

variable "common_tags" {
  type = map(string)
}

variable "tenant_id" {}

variable "ilbIp" {
  default = ""
}

variable "managed_identity_object_id" {
  default = ""
}
