import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import { UserUpsertDialog } from './UserUpsertDialog';

describe('UserUpsertDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <UserUpsertDialog />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  it('shows the upsert button', async () => {
    await renderComponent();
    expect(screen.getByRole('button', { name: /upsert users/i })).toBeInTheDocument();
  });

  it('opens the dialog and shows instructions', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /upsert users/i }));

    expect(screen.getByText(/users can be created or updated via csv/i)).toBeInTheDocument();
    expect(screen.getByText(/allowed csv headers/i)).toBeInTheDocument();
    expect(screen.getByText('Example 1:', { exact: false })).toBeInTheDocument();
    expect(screen.getByText('Example 2:', { exact: false })).toBeInTheDocument();
    expect(screen.getByText('Example 3:', { exact: false })).toBeInTheDocument();
  });

  it('submits CSV and shows results', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /upsert users/i }));

    const textarea = screen.getByRole('textbox');
    await user.type(textarea, 'username,password,email\nandi,pass1,andi@example.com');

    nockJophiel()
      .post('/users/batch-upsert', 'username,password,email\nandi,pass1,andi@example.com')
      .reply(200, {
        createdUsernames: ['andi'],
        updatedUsernames: [],
      });

    await user.click(screen.getByRole('button', { name: /submit/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));

    await waitFor(() => {
      expect(screen.getByText('1 users created, 0 users updated.')).toBeInTheDocument();
    });
    expect(screen.getByText('andi')).toBeInTheDocument();
  });
});
