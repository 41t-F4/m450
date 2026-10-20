# Automation Testing – Übung 3: Backend mit Traffic belasten (Lasttest)

## Tool
Angeschaut habe ich **JMeter** (Thread Groups = virtuelle Benutzer, Ramp-up,
HTML-Reports, CLI für CI – das Tool für realistische Benutzer-Szenarien) und
**Postman Performance Testing** (Collections mit Load-Profilen abspielen).
Gemessen habe ich mit **ab (ApacheBench)** – auf dem Mac vorinstalliert und für
einen einzelnen Endpoint völlig ausreichend:

```bash
ab -n 2000 -c 10  http://localhost:8081/students    # Last
ab -n 2000 -c 100 http://localhost:8081/students    # mehr Parallelität
ab -n 5000 -c 500 http://localhost:8081/students    # Stress
```

## Resultate (lokal)

| Szenario | Requests/s | Ø Latenz | P99 | Fehler |
|---|---|---|---|---|
| GET, c=10 | ~9'400 | 1.1 ms | 3 ms | 0 |
| GET, c=100 | ~25'600 | 3.9 ms | 5 ms | 0 |
| GET, c=500 | ~3'700 | 134 ms | 677 ms | 0 |

## Interpretation
- **Load-Test** (c=10/100): stabil im einstelligen ms-Bereich, 0 Fehler – Verhalten
  bei definierter Last ist ok.
- **Stress-Test** (c=500): Durchsatz bricht ein, P99 springt auf ~0.7 s – die Requests
  stauen sich vor dem Tomcat-Threadpool (Default 200 Threads). Das Limit liegt also
  zwischen c=100 und c=500. Genau das will ein Stress-Test herausfinden.
- Vorbehalt: localhost-Messung ohne Netzwerk – die absoluten Zahlen sagen nichts über
  Produktion, die Trends (wo kippt es) schon. Und: Lasttests brauchen einen konstanten
  Ausgangszustand (DB zurücksetzen), sonst wächst die Antwort-Payload zwischen den Läufen.

## Fazit
Load vs. Stress gehört zum System Testing (nicht-funktional). Für einen Endpoint
reicht `ab`; sobald Szenarien über mehrere Endpoints oder CI-Schwellenwerte gefragt
sind, lohnt sich JMeter.
