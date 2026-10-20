// @ts-check
const { defineConfig, devices } = require('@playwright/test');

// E2E-Setup: Playwright startet Backend (Spring Boot, Port 8081) und
// Frontend (Angular ng serve, Port 4200) selbst - ein einziger Befehl
// (npm test) genuegt, wie es sich fuer automatisierte Tests gehoert.
module.exports = defineConfig({
  testDir: './tests',
  timeout: 30_000,
  // App-Zustand ist geteilt (eine h2-DB) -> Tests seriell, nicht parallel
  workers: 1,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://localhost:4200',
    ...devices['Desktop Chrome'],
    // Bei Fehlschlag: Screenshot + Trace fuer die Analyse
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
  },
  webServer: [
    {
      command: 'cd ../spring-boot-angular-basic-lw2 && ./mvnw -q spring-boot:run',
      url: 'http://localhost:8081/students',
      reuseExistingServer: true,
      timeout: 180_000,
    },
    {
      command: 'cd ../spring-boot-angular-basic-lw2/src/main/js/my-app && npm start',
      url: 'http://localhost:4200',
      reuseExistingServer: true,
      timeout: 180_000,
    },
  ],
});
