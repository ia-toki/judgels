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
      .get('/v2/user-roles')
      .reply(200, {
        data: [
          {
            userJid: 'superadminJid',
            role: { account: 'SUPERADMIN', problem: 'ADMIN', contest: 'ADMIN', training: 'ADMIN' },
          },
          { userJid: 'userJid2', role: { account: null, problem: 'ADMIN', contest: null, training: null } },
          { userJid: 'userJid1', role: { account: 'ADMIN', training: 'ADMIN' } },
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
          <TestRouter initialEntries={['/admin/roles']}>
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
      .map(cell => within(cell).queryByRole('img')?.textContent ?? '');

  test('renders the roles table, superadmin first and then by username', async () => {
    await renderComponent();
    await rowOf('andi');

    const rows = screen.getAllByRole('row').slice(1);
    expect(rows.map(row => within(row).getByRole('link').textContent)).toEqual(['superadmin', 'andi', 'budi']);
    expect(rows.map(renderRoles)).toEqual([
      ['System admin', 'Problem admin', 'Contest admin', 'Training admin'],
      ['System admin', '', '', 'Training admin'],
      ['', 'Problem admin', '', ''],
    ]);
  });

  test('locks superadmin and the own system admin role', async () => {
    await renderComponent();
    const user = userEvent.setup();

    const superadminRow = await rowOf('superadmin');
    expect(within(superadminRow).queryByRole('button')).not.toBeInTheDocument();

    const ownRow = await rowOf('andi');
    expect(within(ownRow).getByRole('button', { name: /remove/i })).toBeDisabled();

    await user.click(within(ownRow).getByRole('button', { name: /edit/i }));
    expect(screen.getByRole('checkbox', { name: /system admin/i })).toBeDisabled();
  });

  test('edits the roles of a user', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(within(await rowOf('budi')).getByRole('button', { name: /edit/i }));
    expect(screen.getByRole('checkbox', { name: /problem admin/i })).toBeChecked();
    await user.click(screen.getByRole('checkbox', { name: /contest admin/i }));

    const scope = nockApi().put('/v2/user-roles/userJid2', { problem: 'ADMIN', contest: 'ADMIN' }).reply(200);
    await user.click(screen.getByRole('button', { name: /save/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));
  });

  test('removes a user', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(within(await rowOf('budi')).getByRole('button', { name: /remove/i }));
    expect(screen.getByText('Remove all roles from budi?')).toBeInTheDocument();

    const scope = nockApi().put('/v2/user-roles/userJid2', {}).reply(200);
    await user.click(screen.getByRole('button', { name: /^remove$/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));
  });

  test('adds users, keeping the ones not found', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /add admins/i }));
    await user.type(screen.getByLabelText(/usernames/i), 'caca\ndodi');
    expect(screen.getByRole('button', { name: /^add$/i })).toBeDisabled();
    await user.click(screen.getByRole('checkbox', { name: /contest admin/i }));

    const scope = nockApi()
      .post('/v2/user-roles/batch-upsert', { usernames: ['caca', 'dodi'], role: { contest: 'ADMIN' } })
      .reply(200, { upsertedUserProfilesMap: { caca: { username: 'caca' } } });
    await user.click(screen.getByRole('button', { name: /^add$/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));

    expect(await screen.findByText('User not found: dodi.')).toBeInTheDocument();
    expect(screen.getByLabelText(/usernames/i)).toHaveValue('dodi');
  });
});
