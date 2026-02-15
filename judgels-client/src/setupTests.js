import '@testing-library/jest-dom/vitest';
import nock from 'nock';
import { TextDecoder, TextEncoder } from 'node:util';
import { afterEach } from 'vitest';
import { vi } from 'vitest';

import { clearSession } from './modules/session';

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

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
  welcomeBanner: {
    title: 'Welcome to Judgels',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit.Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
  },
};

window.scrollTo = function () {
  return;
};

afterEach(() => {
  clearSession();
});
