import { expect, test } from '@playwright/test';

const jsfBaseUrl = process.env.JSF_BASE_URL ?? 'http://localhost:8080';

test.describe('JSF Application (e-commerce-micro)', () => {
  test('should load index page and micro toolbar navigation', async ({ page }) => {
    await page.goto(`${jsfBaseUrl}/jsf/index.xhtml`, { waitUntil: 'domcontentloaded' });

    await expect(page).toHaveTitle(/E-Commerce Micro - Home/);
    await expect(page.getByText('E-Commerce Micro')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Customers' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Orders' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Order Items' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Products' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Reviews' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Who Am I' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Instance Info' })).toBeVisible();
  });

  test('should switch to product view and show product crud card', async ({ page }) => {
    await page.goto(`${jsfBaseUrl}/jsf/index.xhtml`, { waitUntil: 'domcontentloaded' });
    const content = page.locator('#includedView');

    await page.getByRole('button', { name: 'Products' }).click();

    await expect(content.locator('.ui-card-title')).toHaveText(/Product CRUD/);
    await expect(content.getByText('Name:')).toBeVisible();
    await expect(content.getByRole('button', { name: 'Save' })).toBeVisible();
  });

  test('should switch to order view and show order crud card', async ({ page }) => {
    await page.goto(`${jsfBaseUrl}/jsf/index.xhtml`, { waitUntil: 'domcontentloaded' });
    const content = page.locator('#includedView');

    await page.getByRole('button', { name: 'Orders' }).click();

    await expect(content.locator('.ui-card-title')).toHaveText(/Order CRUD/);
    await expect(content.getByText('Customer ID:')).toBeVisible();
    await expect(content.getByText('Total Amount:')).toBeVisible();
    await expect(content.getByRole('button', { name: 'Save' })).toBeVisible();
  });

  test('should switch to instance info view', async ({ page }) => {
    await page.goto(`${jsfBaseUrl}/jsf/index.xhtml`, { waitUntil: 'domcontentloaded' });
    const content = page.locator('#includedView');

    await page.getByRole('button', { name: 'Instance Info' }).click();

    await expect(content.locator('.ui-card-title')).toHaveText(/Payara Instance Info/);
    await expect(content.getByText('Hostname:')).toBeVisible();
    await expect(content.getByText('Session ID:')).toBeVisible();
  });

  test('should switch to who-am-i view', async ({ page }) => {
    await page.goto(`${jsfBaseUrl}/jsf/index.xhtml`, { waitUntil: 'domcontentloaded' });
    const content = page.locator('#includedView');

    await page.getByRole('button', { name: 'Who Am I' }).click();

    await expect(content.locator('.ui-card-title')).toHaveText(/Who Am I/);
    await expect(content.getByText('Username:')).toBeVisible();
  });
});
