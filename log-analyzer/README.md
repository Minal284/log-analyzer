# Log Analyzer (Spring Boot)

## Run
1. IntelliJ me `File > Open` -> `log-analyzer` folder select karein (pom.xml detect hoga).
2. Maven dependencies download hone dein, JDK 17+ set karein.
3. `LogAnalyzerApplication` run karein.
4. Browser: http://localhost:8080 -> `sample-logs/sample.log` upload karein.

## API
POST /api/logs/analyze  (multipart)
- file (required), level, keyword, top (default 5)

curl -F "file=@sample-logs/sample.log" -F "level=ERROR" http://localhost:8080/api/logs/analyze

## Log format
`yyyy-MM-dd HH:mm:ss.SSS LEVEL [thread] logger - message`
Stack trace lines pichli entry se attach ho jaati hain.
