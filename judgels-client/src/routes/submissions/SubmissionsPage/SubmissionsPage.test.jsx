import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../modules/session';
import { QueryClientProviderWrapper } from '../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../test/RouterWrapper';
import { nockApi } from '../../../utils/nock';
import SubmissionsPage from './SubmissionsPage';

describe('SubmissionsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid', username: 'andi' });
  });

  const renderComponent = async page => {
    nockApi()
      .get('/submissions/programming')
      .query(true)
      .reply(200, {
        data: { page, hasPreviousPage: false, hasNextPage: false },
        config: { canManage: false },
        profilesMap: {},
        problemAliasesMap: {},
        problemNamesMap: {},
        containerNamesMap: {},
        containerPathsMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <SubmissionsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders empty placeholder', async () => {
    await renderComponent([]);

    expect(await screen.findByText(/no submissions/i)).toBeInTheDocument();
  });
});
