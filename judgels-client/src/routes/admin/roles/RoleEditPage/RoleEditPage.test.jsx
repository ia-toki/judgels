import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import RoleEditPage from './RoleEditPage';

describe('RoleEditPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid1' });
  });

  const renderComponent = async username => {
    nockApi()
      .get('/v2/user-roles')
      .reply(200, {
        data: [
          { userJid: 'superadminJid', role: { account: 'SUPERADMIN', contest: 'ADMIN' } },
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
          <TestRouter initialEntries={[`/admin/roles/${username}`]} path="/admin/roles/$username">
            <RoleEditPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('locks the own system admin role', async () => {
    await renderComponent('andi');
    expect(await screen.findByRole('checkbox', { name: /system admin/i })).toBeDisabled();
  });

  test('edits the roles of a user', async () => {
    await renderComponent('budi');
    const user = userEvent.setup();

    expect(await screen.findByRole('checkbox', { name: /problem admin/i })).toBeChecked();
    await user.click(screen.getByRole('checkbox', { name: /contest admin/i }));

    const scope = nockApi().put('/v2/user-roles/userJid2', { problem: 'ADMIN', contest: 'ADMIN' }).reply(200);
    await user.click(screen.getByRole('button', { name: /save/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));
  });
});
