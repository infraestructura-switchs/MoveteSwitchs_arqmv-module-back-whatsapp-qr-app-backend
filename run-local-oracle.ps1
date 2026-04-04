# run-local-oracle.ps1
# Loads environment file by priority and starts app with selected profile for Oracle

param(
    [ValidateSet("local", "prod", "test")]
    [string]$Env = "local"
)

$candidateEnvFiles = @(
    ".env.$Env.oracle",
    ".env.oracle",
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
    Write-Host "No .env.oracle or .env file found. Using current shell environment values."
}

[System.Environment]::SetEnvironmentVariable("SERVER_PORT", "8081", "Process")
$activeProfile = if ($Env -eq "test") { "test" } else { "$Env-oracle" }
[System.Environment]::SetEnvironmentVariable("SPRING_PROFILES_ACTIVE", $activeProfile, "Process")

if ($Env -ne "test") {
    [System.Environment]::SetEnvironmentVariable("SPRING_JPA_HIBERNATE_DDL_AUTO", "none", "Process")
}

$oracleUrl = [System.Environment]::GetEnvironmentVariable("ORACLE_DATASOURCE_URL", "Process")
if ([string]::IsNullOrWhiteSpace($oracleUrl)) {
    $oracleUrl = "jdbc:oracle:thin:@//localhost:1521/XEPDB1"
    [System.Environment]::SetEnvironmentVariable("ORACLE_DATASOURCE_URL", $oracleUrl, "Process")
    Write-Host "ORACLE_DATASOURCE_URL not set. Using default: $oracleUrl"
}

$oracleUser = [System.Environment]::GetEnvironmentVariable("ORACLE_DATASOURCE_USERNAME", "Process")
if ([string]::IsNullOrWhiteSpace($oracleUser)) {
    $oracleUser = "system"
    [System.Environment]::SetEnvironmentVariable("ORACLE_DATASOURCE_USERNAME", $oracleUser, "Process")
    Write-Host "ORACLE_DATASOURCE_USERNAME not set. Using default: $oracleUser"
}

$oraclePassword = [System.Environment]::GetEnvironmentVariable("ORACLE_DATASOURCE_PASSWORD", "Process")
if ([string]::IsNullOrWhiteSpace($oraclePassword)) {
    Write-Warning "ORACLE_DATASOURCE_PASSWORD is empty. Continuing with empty password."
}

if ($oracleUrl -notmatch '^jdbc:oracle:thin:@') {
    Write-Error "Invalid ORACLE_DATASOURCE_URL format. Expected it to start with 'jdbc:oracle:thin:@'. Current value: $oracleUrl"
    exit 1
}

if ($Env -ne "test") {
    Write-Host "Starting with profile: $activeProfile on port 8081 (ddl-auto=none)"
} else {
    Write-Host "Starting with profile: $activeProfile on port 8081"
}
.\gradlew.bat bootRun
