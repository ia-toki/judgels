import { act, render, waitFor } from '@testing-library/react';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import ActivatePage from './ActivatePage';

describe('ActivatePage', () => {
  afterEach(() => {
    nock.cleanAll();
  });

  beforeEach(async () => {
    nockJophiel().post('/user-account/activate/code123').reply(200);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/activate/code123']} path="/activate/$emailCode">
            <ActivatePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('activate', async () => {
    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
