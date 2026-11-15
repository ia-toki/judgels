import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../../utils/nock';
import ProblemWriterAddPage from './ProblemWriterAddPage';

describe('ProblemWriterAddPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid1' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/v2/user-roles')
      .reply(200, {
        data: [{ userJid: 'superadminJid', roles: ['SUPERADMIN', 'PROBLEM_WRITER'] }],
        profilesMap: { superadminJid: { username: 'superadmin' } },
      })
      .persist();

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles/problem-writers/-/add']}>
            <ProblemWriterAddPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('adds writers, keeping the users not found', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.type(await screen.findByLabelText(/usernames/i), 'caca\n\n dodi \ncaca');

    const scope = nockApi()
      .post('/v2/user-roles/add', { usernames: ['caca', 'dodi'], roles: ['PROBLEM_WRITER'] })
      .reply(200, { addedUserProfilesMap: { caca: { username: 'caca' } } });
    await user.click(screen.getByRole('button', { name: /^submit$/i }));
    await waitFor(() => expect(scope.isDone()).toBe(true));

    expect(await screen.findByText('User not found: dodi.')).toBeInTheDocument();
    expect(screen.getByLabelText(/usernames/i)).toHaveValue('dodi');
  });

  test('rejects the superadmin', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.type(await screen.findByLabelText(/usernames/i), 'superadmin');
    await user.click(screen.getByRole('button', { name: /^submit$/i }));

    expect(await screen.findByText('superadmin already holds every role.')).toBeInTheDocument();
  });
});
