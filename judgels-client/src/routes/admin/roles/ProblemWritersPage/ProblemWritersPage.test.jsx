import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemWritersPage from './ProblemWritersPage';

describe('ProblemWritersPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ roles = ['PROBLEM_ADMIN'], writers } = {}) => {
    nockApi().get('/v2/user-web/config').reply(200, { roles });
    nockApi()
      .get('/v4/problems/writers')
      .reply(200, {
        data: writers || ['JIDUSER2', 'JIDUSER1'],
        profilesMap: {
          JIDUSER1: { username: 'andi' },
          JIDUSER2: { username: 'budi' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles/problem-writers']} path="/admin/roles/problem-writers">
            <ProblemWritersPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('link', { name: /edit problem writers/i });
  };

  test('writers, by username', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('table')).getAllByRole('row').slice(1);
    expect(rows.map(row => row.textContent)).toEqual(['andi', 'budi']);
  });

  test('no writers', async () => {
    await renderComponent({ writers: [] });

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

  test('links to the edit page', async () => {
    await renderComponent();

    expect(screen.getByRole('link', { name: /edit problem writers/i })).toHaveAttribute(
      'href',
      '/admin/roles/problem-writers/edit'
    );
    expect(screen.queryByRole('link', { name: /upsert admins/i })).not.toBeInTheDocument();
  });
});
