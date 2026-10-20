// @ts-check
const { test, expect } = require('@playwright/test');

// E2E-Tests: echter Browser, echtes Angular-Frontend, echtes Backend inkl. h2.
// Bewusst nur die zwei wichtigsten User-Flows (Testpyramide: wenige E2E-Tests).

test('Studentenliste zeigt die geseedeten Studenten an', async ({ page }) => {
  await page.goto('/students');

  await expect(page.getByRole('columnheader', { name: 'Name' })).toBeVisible();

  // exact: true noetig, sonst matcht 'Jonas' auch die Email-Zelle 'jonas@tbz.ch'
  await expect(page.getByRole('cell', { name: 'Jonas', exact: true })).toBeVisible();

  // Mindestens die 5 Seed-Studenten als Zeilen vorhanden
  const rows = page.locator('tbody tr');
  expect(await rows.count()).toBeGreaterThanOrEqual(5);
});

test('Student erfassen: Formular -> Redirect -> erscheint in der Liste', async ({ page }) => {
  await page.goto('/addstudents');

  // Eindeutiger Name, damit der Test wiederholbar bleibt
  const name = `Nicola E2E ${Date.now()}`;

  await page.getByPlaceholder('Enter your name').fill(name);
  await page.getByPlaceholder('Enter your email address').fill('nicola.e2e@tbz.ch');
  await page.getByRole('button', { name: 'Submit' }).click();

  // Nach dem Submit: Redirect auf die Liste, neuer Student in der Tabelle
  // (Roundtrip GUI -> POST -> DB -> GET -> GUI)
  await expect(page).toHaveURL(/\/students$/);
  await expect(page.getByRole('cell', { name })).toBeVisible();
});
