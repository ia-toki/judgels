import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import RolesPage from './RolesPage';

describe('RolesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  let roles;
  let profilesMap;

  const renderComponent = async () => {
    nockJophiel().get('/user-roles').reply(200, {
      data: roles,
      profilesMap,
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles']}>
            <RolesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('when there are no roles', () => {
    beforeEach(async () => {
      roles = [];
      profilesMap = {};
      await renderComponent();
    });

    it('shows placeholder text', async () => {
      expect(await screen.findByText(/no roles/i)).toBeInTheDocument();
    });
  });

  describe('when there are roles', () => {
    beforeEach(async () => {
      roles = [
        { userJid: 'userJid1', role: { jophiel: 'ADMIN', sandalphon: 'ADMIN', uriel: 'ADMIN', jerahmeel: 'ADMIN' } },
        { userJid: 'userJid2', role: { sandalphon: 'ADMIN' } },
      ];
      profilesMap = {
        userJid1: { username: 'andi' },
        userJid2: { username: 'budi' },
      };
      await renderComponent();
    });

    it('shows the roles table', async () => {
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
      ).toEqual([[], ['andi', 'ADMIN', 'ADMIN', 'ADMIN', 'ADMIN'], ['budi', '-', 'ADMIN', '-', '-']]);
    });

    it('shows the edit button', () => {
      expect(screen.getByRole('button', { name: /edit roles/i })).toBeInTheDocument();
    });
  });
});
