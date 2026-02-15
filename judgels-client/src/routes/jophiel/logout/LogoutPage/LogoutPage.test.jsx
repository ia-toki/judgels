import { act, render } from '@testing-library/react';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import LogoutPage from './LogoutPage';

import * as logoutActions from '../modules/logoutActions';

vi.mock('../modules/logoutActions');

describe('LogoutPage', () => {
  beforeEach(async () => {
    logoutActions.logOut.mockReturnValue(Promise.resolve());

    await act(async () =>
      render(
        <TestRouter>
          <LogoutPage />
        </TestRouter>
      )
    );
  });

  it('logs out immediately', () => {
    expect(logoutActions.logOut).toHaveBeenCalled();
  });
});
