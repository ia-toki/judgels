import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import RolesPage from './RolesPage';

describe('RolesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid1' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/v2/user-web/config')
      .reply(200, { roles: ['SYSTEM_ADMIN', 'TRAINING_ADMIN'] });
    nockApi()
      .get('/v2/user-roles')
      .reply(200, {
        data: [
          {
            userJid: 'superadminJid',
            roles: ['SUPERADMIN', 'SYSTEM_ADMIN', 'PROBLEM_ADMIN', 'CONTEST_ADMIN', 'TRAINING_ADMIN'],
          },
          { userJid: 'userJid2', roles: ['PROBLEM_ADMIN'] },
          { userJid: 'userJid1', roles: ['SYSTEM_ADMIN', 'TRAINING_ADMIN'] },
        ],
        profilesMap: {
          superadminJid: { username: 'superadmin' },
          userJid1: { username: 'andi' },
          userJid2: { username: 'budi' },
        },
      })
      .persist();

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles/admins']}>
            <RolesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const rowOf = async username => (await screen.findByRole('link', { name: username })).closest('tr');

  const renderRoles = row =>
    within(row)
      .queryAllByRole('cell')
      .slice(1, -1)
      .map(cell => cell.textContent);

  test('renders the roles table by username, without superadmin', async () => {
    await renderComponent();
    await rowOf('andi');

    const rows = screen.getAllByRole('row').slice(1);
    expect(rows.map(row => within(row).getAllByRole('link')[0].textContent)).toEqual(['andi', 'budi']);
    expect(rows.map(renderRoles)).toEqual([
      ['ADMIN', '', '', 'ADMIN'],
      ['', 'ADMIN', '', ''],
    ]);
  });

  test('locks removing the own roles', async () => {
    await renderComponent();
    expect(within(await rowOf('andi')).getByRole('button', { name: /remove/i })).toBeDisabled();
  });

  test('links to the edit page of each user', async () => {
    await renderComponent();
    expect(within(await rowOf('budi')).getByRole('link', { name: /edit/i })).toHaveAttribute(
      'href',
      '/admin/roles/admins/budi'
    );
  });

  test('removes a user', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(within(await rowOf('budi')).getByRole('button', { name: /remove/i }));
    expect(screen.getByText('Remove all roles from budi?')).toBeInTheDocument();

    const scope = nockApi().put('/v2/user-roles/userJid2', []).reply(200);
    await user.click(screen.getByRole('button', { name: /^remove$/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));
  });

  test('links to the upsert page', async () => {
    await renderComponent();
    expect(await screen.findByRole('link', { name: /upsert admins/i })).toHaveAttribute(
      'href',
      '/admin/roles/admins/-/upsert'
    );
  });
});
