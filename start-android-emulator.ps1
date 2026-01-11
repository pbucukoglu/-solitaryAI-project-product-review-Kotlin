# Android Emulator Starter Script
# This script starts the Android emulator and waits for it to be ready

Write-Host "🚀 Starting Android Emulator..." -ForegroundColor Green

$emulatorPath = "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe"
$avdName = "Pixel_2_API_34"

# Check if emulator exists
if (-not (Test-Path $emulatorPath)) {
    Write-Host "❌ Error: Android emulator not found at: $emulatorPath" -ForegroundColor Red
    Write-Host "Please install Android Studio and create an AVD." -ForegroundColor Yellow
    exit 1
}

# Check if emulator is already running
$devices = adb devices
if ($devices -match "emulator") {
    Write-Host "✅ Emulator is already running!" -ForegroundColor Green
    adb devices
    exit 0
}

# Start emulator in background
Write-Host "Starting $avdName..." -ForegroundColor Yellow
Start-Process -FilePath $emulatorPath -ArgumentList "-avd", $avdName -WindowStyle Normal

# Wait for emulator to boot
Write-Host "⏳ Waiting for emulator to boot (this may take 1-2 minutes)..." -ForegroundColor Yellow
Write-Host "   Please wait until you see the Android home screen." -ForegroundColor Gray

# Wait for device to be ready
$maxWait = 120 # 2 minutes
$waited = 0
while ($waited -lt $maxWait) {
    Start-Sleep -Seconds 5
    $waited += 5
    $devices = adb devices 2>&1
    if ($devices -match "emulator-5554\s+device") {
        Write-Host "✅ Emulator is ready!" -ForegroundColor Green
        adb devices
        Write-Host ""
        Write-Host "Now you can run:" -ForegroundColor Cyan
        Write-Host "  cd mobile" -ForegroundColor White
        Write-Host "  npx expo start --android" -ForegroundColor White
        exit 0
    }
    Write-Host "   Still waiting... ($waited/$maxWait seconds)" -ForegroundColor Gray
}

Write-Host "❌ Timeout: Emulator did not start within $maxWait seconds" -ForegroundColor Red
Write-Host "Please check Android Studio or start the emulator manually." -ForegroundColor Yellow
exit 1

