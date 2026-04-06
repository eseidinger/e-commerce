import { expect, test } from '@playwright/test';

const angularBaseUrl = process.env.ANGULAR_BASE_URL ?? 'http://localhost:4200';

test.describe('Angular Application', () => {
  test('should load shell and menu entries', async ({ page }) => {
    await page.goto(angularBaseUrl, { waitUntil: 'domcontentloaded' });

    await expect(page.getByText('e-commerce-ng')).toBeVisible();
    await expect(page.getByRole('link', { name: 'Customers' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Products' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Orders' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Order Items' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Reviews' })).toBeVisible();
  });

  test('should navigate to products and show product form', async ({ page }) => {
    await page.goto(angularBaseUrl, { waitUntil: 'domcontentloaded' });

    await page.getByRole('link', { name: 'Products' }).click();

    await expect(page).toHaveURL(/\/products$/);
    await expect(page.getByText('Product CRUD')).toBeVisible();
    await expect(page.getByPlaceholder('Product Name')).toBeVisible();
    await expect(page.getByPlaceholder('Description')).toBeVisible();
  });

  test('should navigate to orders and show order form controls', async ({ page }) => {
    await page.goto(angularBaseUrl, { waitUntil: 'domcontentloaded' });

    await page.getByRole('link', { name: 'Orders' }).click();

    await expect(page).toHaveURL(/\/orders$/);
    await expect(page.getByText('Order CRUD')).toBeVisible();
    await expect(page.getByPlaceholder('Customer ID')).toBeVisible();
    await expect(page.getByPlaceholder('Total Amount')).toBeVisible();
  });
});
