import nock from 'nock';
import { vi } from 'vitest';

import { nockJophiel } from '../../../../utils/nock';

import * as resetPasswordActions from './resetPasswordActions';

const mockPush = vi.fn();
const mockReplace = vi.fn();

vi.mock('../../../../modules/navigation/navigationRef', () => ({
  getNavigationRef: () => ({
    push: mockPush,
    replace: mockReplace,
  }),
}));

const emailCode = 'code123';
const newPassword = 'pass';

describe('resetPasswordActions', () => {
  beforeEach(() => {
    mockPush.mockClear();
    mockReplace.mockClear();
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('resetPassword()', () => {
    describe('when the email code is valid', () => {
      it('succeeds', async () => {
        nockJophiel().post(`/user-account/reset-password`, { emailCode, newPassword }).reply(200);

        await resetPasswordActions.resetPassword(emailCode, newPassword);

        expect(mockPush).toHaveBeenCalledWith('/login');
      });
    });

    describe('when the email code is invalid', () => {
      it('throws a more descriptive error', async () => {
        nockJophiel().post(`/user-account/reset-password`, { emailCode, newPassword }).reply(400);

        await expect(resetPasswordActions.resetPassword(emailCode, newPassword)).rejects.toEqual(
          new Error('Invalid code.')
        );
      });
    });
  });
});
