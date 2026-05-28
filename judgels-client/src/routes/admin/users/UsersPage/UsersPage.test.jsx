import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import UsersPage from './UsersPage';

describe('UsersPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    users = [
      { jid: 'userJid1', username: 'andi', email: 'andi@example.com' },
      { jid: 'userJid2', username: 'budi', email: 'budi@example.com' },
    ],
    lastSessionTimesMap = {
      userJid1: '2024-01-15T10:00:00Z',
    },
  } = {}) => {
    nockJophiel()
      .get('/users')
      .query(true)
      .reply(200, {
        data: {
          page: users,
          totalCount: users.length,
        },
        lastSessionTimesMap,
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/users']}>
            <UsersPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no users', async () => {
    await renderComponent({ users: [], lastSessionTimesMap: {} });
    expect(await screen.findByText(/no users/i)).toBeInTheDocument();
  });

  test('renders the users table', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(
      rows.map(row =>
        within(row)
          .queryAllByRole('cell')
          .map(cell => cell.textContent)
      )
    ).toEqual([[], ['andi', 'andi@example.com', expect.any(String)], ['budi', 'budi@example.com', '-']]);
  });

  test('renders the upsert button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /upsert users/i })).toBeInTheDocument();
  });
});
