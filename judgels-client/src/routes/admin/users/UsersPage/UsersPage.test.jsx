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

  let users;
  let lastSessionTimesMap;

  const renderComponent = async () => {
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

  describe('when there are no users', () => {
    beforeEach(async () => {
      users = [];
      lastSessionTimesMap = {};
      await renderComponent();
    });

    it('shows placeholder text', async () => {
      expect(await screen.findByText(/no users/i)).toBeInTheDocument();
    });
  });

  describe('when there are users', () => {
    beforeEach(async () => {
      users = [
        { jid: 'userJid1', username: 'andi', email: 'andi@example.com' },
        { jid: 'userJid2', username: 'budi', email: 'budi@example.com' },
      ];
      lastSessionTimesMap = {
        userJid1: '2024-01-15T10:00:00Z',
      };
      await renderComponent();
    });

    it('shows the users table', async () => {
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

    it('shows the upsert button', () => {
      expect(screen.getByRole('button', { name: /upsert users/i })).toBeInTheDocument();
    });
  });
});
