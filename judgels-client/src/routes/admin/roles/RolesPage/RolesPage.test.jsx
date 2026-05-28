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

  const renderComponent = async ({
    roles = [
      { userJid: 'userJid1', role: { jophiel: 'ADMIN', sandalphon: 'ADMIN', uriel: 'ADMIN', jerahmeel: 'ADMIN' } },
      { userJid: 'userJid2', role: { sandalphon: 'ADMIN' } },
    ],
    profilesMap = {
      userJid1: { username: 'andi' },
      userJid2: { username: 'budi' },
    },
  } = {}) => {
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

  test('renders placeholder when there are no roles', async () => {
    await renderComponent({ roles: [], profilesMap: {} });
    expect(await screen.findByText(/no roles/i)).toBeInTheDocument();
  });

  test('renders the roles table', async () => {
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
    ).toEqual([[], ['andi', 'ADMIN', 'ADMIN', 'ADMIN', 'ADMIN'], ['budi', '-', 'ADMIN', '-', '-']]);
  });

  test('renders the edit button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /edit roles/i })).toBeInTheDocument();
  });
});
