# Importar historias a Jira (CSV)

Pasos rápidos para importar el CSV `docs/jira_stories.csv` al proyecto Jira (Cloud o Server):

1. Crea el Sprint en tu board (opcional, puedes asignarlas después).
2. En Jira: Settings (⚙️) → System → External System Import → CSV.
3. Sube `docs/jira_stories.csv` y mapea las columnas:
   - Summary → Summary
   - Issue Type → Issue Type
   - Description → Description
   - Priority → Priority
   - Labels → Labels
   - Story Points → Story Points (campo custom, mapear manualmente)
   - Sprint → Sprint (si el sprint ya existe, asignará las historias)
4. Ejecuta la importación y revisa las issues creadas en el board.
5. Ajusta estimaciones o asignaciones si es necesario.

Notas:
- Si tu Jira tiene un campo de Story Points con otro `customfield_xxxxx`, mapea esa columna manualmente durante el import.
- Si quieres crear las issues directamente via API (automatizado), puedo generar un script que lea este CSV y haga POST a la API de Jira (necesitaré Project Key y un API token).
