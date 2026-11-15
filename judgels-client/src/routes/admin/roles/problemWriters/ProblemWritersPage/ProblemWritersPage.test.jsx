import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { setSession } from '../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../utils/nock';
import ProblemWritersPage from './ProblemWritersPage';

describe('ProblemWritersPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const defaultUsers = [
    { userJid: 'superadminJid', roles: ['SUPERADMIN', 'PROBLEM_WRITER'] },
    { userJid: 'JIDUSER2', roles: ['CONTEST_ADMIN', 'PROBLEM_WRITER'] },
    { userJid: 'JIDUSER1', roles: ['PROBLEM_WRITER'] },
    { userJid: 'JIDUSER3', roles: ['TRAINING_ADMIN'] },
  ];

  const renderComponent = async ({ roles = ['PROBLEM_ADMIN'], users = defaultUsers } = {}) => {
    nockApi().get('/v2/user-web/config').reply(200, { roles });
    nockApi()
      .get('/v2/user-roles')
      .reply(200, {
        data: users,
        profilesMap: {
          superadminJid: { username: 'superadmin' },
          JIDUSER1: { username: 'andi' },
          JIDUSER2: { username: 'budi' },
          JIDUSER3: { username: 'caca' },
        },
      })
      .persist();

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles/problem-writers']} path="/admin/roles/problem-writers">
            <ProblemWritersPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('link', { name: /add problem writers/i });
  };

  test('writers, by username, without superadmin', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('table')).getAllByRole('row').slice(1);
    expect(rows.map(row => within(row).getAllByRole('cell')[0].textContent)).toEqual(['andi', 'budi']);
  });

  test('no writers', async () => {
    await renderComponent({ users: [] });

    expect(screen.getByText('No writers.')).toBeInTheDocument();
    expect(screen.queryByRole('table')).not.toBeInTheDocument();
  });

  test('shows both tabs to a user who is also a system admin', async () => {
    await renderComponent({ roles: ['SYSTEM_ADMIN', 'PROBLEM_ADMIN'] });

    expect(screen.getByRole('link', { name: 'Admins' })).toHaveAttribute('href', '/admin/roles/admins');
    expect(screen.getByRole('link', { name: 'Problem Writers' })).toHaveAttribute(
      'href',
      '/admin/roles/problem-writers'
    );
  });

  test('shows no tabs to a problem admin only', async () => {
    await renderComponent();

    expect(screen.queryByRole('link', { name: 'Admins' })).not.toBeInTheDocument();
  });

  test('links to the add page', async () => {
    await renderComponent();

    expect(screen.getByRole('link', { name: /add problem writers/i })).toHaveAttribute(
      'href',
      '/admin/roles/problem-writers/-/add'
    );
    expect(screen.queryByRole('link', { name: /add admins/i })).not.toBeInTheDocument();
  });

  test('removes a writer, keeping their other roles', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: 'Remove budi' }));
    expect(screen.getByText('Remove budi from problem writers?')).toBeInTheDocument();

    const scope = nockApi().put('/v2/user-roles/JIDUSER2', ['CONTEST_ADMIN']).reply(200);
    await user.click(screen.getByRole('button', { name: /^remove$/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));
  });
});
