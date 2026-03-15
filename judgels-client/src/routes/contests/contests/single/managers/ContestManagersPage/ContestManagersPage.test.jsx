import { act, render, screen, waitFor } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestManagersPage from './ContestManagersPage';

describe('ContestManagersPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ managers = [], canManage = false } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/managers')
      .reply(200, {
        data: {
          page: managers,
          totalCount: managers.length,
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
          <TestRouter initialEntries={['/contests/contest-slug/managers']} path="/contests/$contestSlug/managers">
            <ContestManagersPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders no action buttons when not canManage', async () => {
    await renderComponent();
    await screen.findByRole('heading', { name: 'Managers' });
    expect(screen.queryByRole('button', { name: /add managers/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /remove managers/i })).not.toBeInTheDocument();
  });

  test('renders action buttons when canManage', async () => {
    await renderComponent({ canManage: true });
    expect(await screen.findByRole('button', { name: /add managers/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /remove managers/i })).toBeInTheDocument();
  });

  test('renders placeholder text when there are no managers', async () => {
    await renderComponent();
    expect(await screen.findByText(/no managers/i)).toBeInTheDocument();
    expect(screen.queryByRole('row')).not.toBeInTheDocument();
  });

  test('renders the managers when there are managers', async () => {
    await renderComponent({
      managers: [{ userJid: 'userJid1' }, { userJid: 'userJid2' }],
    });
    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row').slice(1);
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['username1'],
      ['username2'],
    ]);
  });
});
