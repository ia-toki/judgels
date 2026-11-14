import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../../utils/nock';
import ProblemWritersEditPage from './ProblemWritersEditPage';

describe('ProblemWritersEditPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/v4/problems/writers')
      .reply(200, {
        data: ['JIDUSER2', 'JIDUSER1'],
        profilesMap: {
          JIDUSER1: { username: 'andi' },
          JIDUSER2: { username: 'budi' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles/problem-writers/edit']} path="/admin/roles/problem-writers/edit">
            <ProblemWritersEditPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    return await screen.findByRole('textbox');
  };

  test('saves the writers', async () => {
    const usernames = await renderComponent();
    expect(usernames).toHaveValue('andi\nbudi');

    const user = userEvent.setup();
    await user.clear(usernames);
    await user.type(usernames, 'budi\n\n caca \nbudi\n');

    nockApi()
      .put('/v4/problems/writers', [{ username: 'budi' }, { username: 'caca' }])
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows which writers are not found', async () => {
    const usernames = await renderComponent();

    const user = userEvent.setup();
    await user.type(usernames, '\nghost');

    nockApi()
      .put('/v4/problems/writers')
      .reply(400, { message: 'ProblemWriterUsernamesNotFound', args: { usernames: 'ghost' } });

    await user.click(screen.getByRole('button', { name: /save/i }));

    expect(await screen.findByText('Users not found: ghost')).toBeInTheDocument();
  });
});
