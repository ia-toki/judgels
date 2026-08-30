import '@testing-library/jest-dom/vitest';
import nock from 'nock';
import { Blob as NodeBlob, File as NodeFile } from 'node:buffer';
import { TextDecoder, TextEncoder } from 'node:util';
import { afterEach } from 'vitest';
import { vi } from 'vitest';

import { clearSession } from './modules/session';

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

global.Blob = NodeBlob;
global.File = NodeFile;
global.FormData = (
  await new Response('', { headers: { 'content-type': 'application/x-www-form-urlencoded' } }).formData()
).constructor;

nock.disableNetConnect();

vi.mock('./modules/toast/toastActions', () => ({
  showToast: vi.fn(),
  showSuccessToast: vi.fn(),
  showAlertToast: vi.fn(),
  showErrorToast: vi.fn(),
  toastActions: {
    showToast: vi.fn(),
    showSuccessToast: vi.fn(),
    showAlertToast: vi.fn(),
    showErrorToast: vi.fn(),
  },
}));

window.conf = {
  mode: 'TLX',
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrl: 'http://api',
};

window.scrollTo = function () {
  return;
};

afterEach(() => {
  nock.cleanAll();
  clearSession();
});
