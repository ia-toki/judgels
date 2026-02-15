import { act, render } from '@testing-library/react';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import ActivatePage from './ActivatePage';

import * as activateActions from '../modules/activateActions';

vi.mock('../modules/activateActions');

describe('ActivatePage', () => {
  beforeEach(async () => {
    activateActions.activateUser.mockReturnValue(Promise.resolve());

    await act(async () =>
      render(
        <TestRouter initialEntries={['/activate/code123']} path="/activate/$emailCode">
          <ActivatePage />
        </TestRouter>
      )
    );
  });

  test('activate', () => {
    expect(activateActions.activateUser).toHaveBeenCalledWith('code123');
  });
});
