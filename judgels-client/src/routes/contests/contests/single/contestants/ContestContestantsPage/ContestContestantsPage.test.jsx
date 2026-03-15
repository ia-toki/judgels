import { act, render, screen, waitFor } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestContestantsPage from './ContestContestantsPage';

describe('ContestContestantsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    contestants = [{ userJid: 'userJid1' }, { userJid: 'userJid2' }],
    canManage,
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/contestants')
      .reply(200, {
        data: {
          page: contestants,
          totalCount: contestants.length,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        config: {
          canManage,
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/contestants']} path="/contests/$contestSlug/contestants">
            <ContestContestantsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders no buttons when not canManage', async () => {
    await renderComponent({ contestants: [], canManage: false });
    await screen.findByRole('heading', { name: 'Contestants' });
    expect(screen.queryByRole('button', { name: /add contestants/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /remove contestants/i })).not.toBeInTheDocument();
  });

  test('renders action buttons when canManage', async () => {
    await renderComponent({ canManage: true });
    expect(await screen.findByRole('button', { name: /add contestants/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /remove contestants/i })).toBeInTheDocument();
  });

  test('renders placeholder when there are no contestants', async () => {
    await renderComponent({ contestants: [] });
    expect(await screen.findByText(/no contestants/i)).toBeInTheDocument();
    expect(screen.queryByRole('row')).not.toBeInTheDocument();
  });

  test('renders the contestants when there are contestants', async () => {
    await renderComponent();
    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      [],
      ['1', 'username1'],
      ['2', 'username2'],
    ]);
  });
});
