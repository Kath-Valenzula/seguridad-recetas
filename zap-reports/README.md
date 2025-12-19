# OWASP ZAP (Baseline) – Evidencia

Este directorio contiene los reportes generados por **OWASP ZAP Baseline Scan** (DAST) contra la aplicación en ejecución.

## Prerrequisitos

- Docker Desktop funcionando
- Aplicación levantada localmente:
  - Frontend: http://localhost:8081
  - Backend: http://localhost:8082 (si aplica)

## Comando usado (Windows + Docker Desktop)

Ejecutar desde la raíz del repo:

```powershell
$workDir=(Resolve-Path .\zap-reports).Path

docker run --rm -t -v "${workDir}:/zap/wrk" zaproxy/zap-stable \
  zap-baseline.py -t http://host.docker.internal:8081 -m 2 \
  -r zap-report.html -J zap-report.json -w zap-warn.md -x zap-report.xml
```

## Archivos generados

- `zap-report.html`: reporte legible (principal para capturas)
- `zap-warn.md`: resumen de alertas en Markdown
- `zap-report.json` / `zap-report.xml`: formatos exportables

## Nota sobre el código de salida

`zap-baseline.py` puede terminar con **exit code 1** si encuentra alertas tipo `WARN-NEW`.
Eso no implica necesariamente vulnerabilidades críticas, pero sí hallazgos que deben documentarse y, cuando sea posible, mitigarse.
