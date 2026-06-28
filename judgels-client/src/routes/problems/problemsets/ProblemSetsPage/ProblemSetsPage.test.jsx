import { act, render, screen, waitFor } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemSetsPage from './ProblemSetsPage';

describe('ProblemSetsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async response => {
    nockApi().get('/problemsets').query(true).reply(200, response);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ProblemSetsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders problemsets', async () => {
    await renderComponent({
      data: {
        page: [{ jid: 'JIDPS1', slug: 'ps-1', name: 'Problemset One', archiveJid: 'JIDARC1', description: '' }],
        totalCount: 1,
      },
      archiveDescriptionsMap: {},
      problemSetProgressesMap: {},
      profilesMap: {},
    });

    expect(await screen.findByText('Problemset One')).toBeInTheDocument();
    expect(screen.getByText(/most recently added problemsets/i)).toBeInTheDocument();
  });

  test('renders empty placeholder', async () => {
    await renderComponent({
      data: { page: [], totalCount: 0 },
      archiveDescriptionsMap: {},
      problemSetProgressesMap: {},
      profilesMap: {},
    });

    await waitFor(() => expect(screen.getByText(/no problemsets/i)).toBeInTheDocument());
  });
});
