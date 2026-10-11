import { chromium } from 'playwright';

const BASE_URL = process.env.JUDGELS_URL || 'http://localhost:3000';
const DEFAULT_USERNAME = process.env.JUDGELS_USERNAME || 'superadmin';
const DEFAULT_PASSWORD = process.env.JUDGELS_PASSWORD || 'superadmin';

// Logs in through the login form, so that the session ends up in localStorage exactly as the client writes it.
export async function logIn(page, { username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD } = {}) {
  try {
    await page.goto(`${BASE_URL}/login`);
    await page.getByLabel('Username or Email').fill(username, { timeout: 10000 });
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log in' }).click();
    await page.locator('[data-key="username"]', { hasText: username }).waitFor({ timeout: 10000 });
  } catch (cause) {
    throw new Error(
      `Could not log in as ${username} at ${BASE_URL}. Are the client and the server running, and is the password right?`,
      { cause }
    );
  }
}

// Opens a browser on the locally running client, logged in unless `username` is null.
export async function openApp({ username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD, headless = true } = {}) {
  const browser = await chromium.launch({ headless });
  const page = await browser.newPage({ baseURL: BASE_URL });

  if (username !== null) {
    await logIn(page, { username, password });
  }
  return { browser, page };
}
