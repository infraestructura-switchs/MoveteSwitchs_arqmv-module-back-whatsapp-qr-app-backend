# run-local.ps1
# Load .env file
if (Test-Path .env) {
    Get-Content .env | ForEach-Object {
        if ($_ -match '^([^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            # Remove optional quotes
            if ($value -match "^'(.*)'$") { $value = $matches[1] }
            elseif ($value -match '^"(.*)"$') { $value = $matches[1] }
            [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

# Force port 8081 to avoid conflict on 8080
[System.Environment]::SetEnvironmentVariable("SERVER_PORT", "8081", "Process")

# Run the project
.\gradlew.bat bootRun
