param(
    [ValidateSet('collections', 'app', 'crud', 'interface')][string]$Modo = 'collections',
    [string]$BlueJ = 'C:\Program Files\BlueJ'
)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $java = Join-Path $BlueJ 'jdk\bin\java.exe'
    $javac = Join-Path $BlueJ 'jdk\bin\javac.exe'
    if (!(Test-Path -LiteralPath $javac)) { throw 'Informe a pasta do BlueJ usando -BlueJ.' }
    $fx = @('base','graphics','controls') | ForEach-Object {
        $jar = Get-ChildItem -LiteralPath (Join-Path $BlueJ 'lib') -Filter "javafx-$_-*-win.jar" | Select-Object -First 1
        if (!$jar) { throw "Biblioteca JavaFX ausente: $_" }
        $jar.FullName
    }
    $modulos = $fx -join ';'
    New-Item -ItemType Directory -Path 'build' -Force | Out-Null
    $fontes = Get-ChildItem -Path '.', 'model', 'view', 'controller', 'exceptions' -Filter '*.java' -File | Select-Object -ExpandProperty FullName
    & $javac -encoding UTF-8 --module-path $modulos --add-modules javafx.controls -cp '+libs\*' -d build $fontes
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }
    $classe = switch ($Modo) { 'collections' { 'TesteUsuarioCollections' } 'crud' { 'MainTesteCRUD' } 'interface' { 'TesteInterface' } default { 'CampusHubInterface' } }
    & $java --module-path $modulos --add-modules javafx.controls -cp 'build;+libs\*' $classe
    if ($LASTEXITCODE -ne 0) { throw "Falha na execução de $classe." }
} finally {
    Pop-Location
}
