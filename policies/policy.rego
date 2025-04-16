package terraform.deny

deny[reason] {
  input.resource_changes[_].type == "aws_s3_bucket"
  input.resource_changes[_].change.after.acl == "public-read"
  reason := "Public S3 bucket is not allowed."
}
