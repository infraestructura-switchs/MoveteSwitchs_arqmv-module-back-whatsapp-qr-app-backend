# run-local.ps1
# Wrapper script to run profile by db and environment

param(
    [ValidateSet("mysql", "oracle")]
    [string]$Db = "mysql",
    [ValidateSet("local", "prod", "test")]
    [string]$Env = "local"
)

if ($Db -eq "oracle") {
    Write-Host "Running profile '$Env' with Oracle..."
    & .\run-local-oracle.ps1 -Env $Env
} else {
    Write-Host "Running profile '$Env' with MySQL..."
    & .\run-local-mysql.ps1 -Env $Env
}
