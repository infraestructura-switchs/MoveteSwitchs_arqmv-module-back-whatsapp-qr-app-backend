# run-local-mysql.ps1
# Loads environment file by priority and starts app with selected profile for MySQL

param(
    [ValidateSet("local", "prod", "test")]
    [string]$Env = "local"
)

$candidateEnvFiles = @(
    ".env.$Env.mysql",
    ".env.mysql",
    ".env.$Env",
    ".env"
)

$envFile = $null
foreach ($candidate in $candidateEnvFiles) {
    if (Test-Path $candidate) {
        $envFile = $candidate
        break
    }
}

if ($envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^([^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            if ($value -match "^'(.*)'$") { $value = $matches[1] }
            elseif ($value -match '^(".*")$') { $value = $value.Trim('"') }
            [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
    Write-Host "Loaded environment from $envFile"
} else {
    Write-Host "No .env.mysql or .env file found. Using current shell environment values."
}

$serverPort = [System.Environment]::GetEnvironmentVariable("SERVER_PORT", "Process")
if ([string]::IsNullOrWhiteSpace($serverPort)) {
[System.Environment]::SetEnvironmentVariable("SERVER_PORT", "8081", "Process")
}
$activeProfile = if ($Env -eq "test") { "test" } else { "$Env-mysql" }
[System.Environment]::SetEnvironmentVariable("SPRING_PROFILES_ACTIVE", $activeProfile, "Process")

$mysqlUrl = [System.Environment]::GetEnvironmentVariable("SPRING_DATASOURCE_URL", "Process")
if ([string]::IsNullOrWhiteSpace($mysqlUrl)) {
    $mysqlUrl = "jdbc:mysql://localhost:3306/movetedatabase_local"
    [System.Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_URL", $mysqlUrl, "Process")
    Write-Host "SPRING_DATASOURCE_URL not set. Using default: $mysqlUrl"
}

$mysqlUser = [System.Environment]::GetEnvironmentVariable("SPRING_DATASOURCE_USERNAME", "Process")
if ([string]::IsNullOrWhiteSpace($mysqlUser)) {
    $mysqlUser = "root"
    [System.Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_USERNAME", $mysqlUser, "Process")
    Write-Host "SPRING_DATASOURCE_USERNAME not set. Using default: $mysqlUser"
}

$mysqlPassword = [System.Environment]::GetEnvironmentVariable("SPRING_DATASOURCE_PASSWORD", "Process")
if ([string]::IsNullOrWhiteSpace($mysqlPassword)) {
    Write-Warning "SPRING_DATASOURCE_PASSWORD is empty. Continuing with empty password."
}

$productsServiceUrl = [System.Environment]::GetEnvironmentVariable("PRODUCTS_SERVICE_URL", "Process")
if ([string]::IsNullOrWhiteSpace($productsServiceUrl)) {
    $productsServiceUrl = "https://gfvsgktksbutb3pdokevf6vhbi.apigateway.us-phoenix-1.oci.customer-oci.com/arq-prd-v3/"
    [System.Environment]::SetEnvironmentVariable("PRODUCTS_SERVICE_URL", $productsServiceUrl, "Process")
    Write-Host "PRODUCTS_SERVICE_URL not set. Using default: $productsServiceUrl"
} else {
    Write-Host "PRODUCTS_SERVICE_URL set to $productsServiceUrl"
}

if ($mysqlUrl -notmatch '^jdbc:mysql://') {
    Write-Error "Invalid SPRING_DATASOURCE_URL format. Expected it to start with 'jdbc:mysql://'. Current value: $mysqlUrl"
    exit 1
}

Write-Host "Starting with profile: $activeProfile on port 8081"
.\gradlew.bat bootRun
