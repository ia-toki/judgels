import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import { RoleEditDialog } from './RoleEditDialog';

describe('RoleEditDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const currentData = {
    data: [
      { userJid: 'userJid1', role: { jophiel: 'ADMIN', sandalphon: 'ADMIN', uriel: 'ADMIN', jerahmeel: 'ADMIN' } },
      { userJid: 'userJid2', role: { sandalphon: 'ADMIN' } },
    ],
    profilesMap: {
      userJid1: { username: 'andi' },
      userJid2: { username: 'budi' },
    },
  };

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <RoleEditDialog currentData={currentData} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  it('shows the edit button', async () => {
    await renderComponent();
    expect(screen.getByRole('button', { name: /edit roles/i })).toBeInTheDocument();
  });

  it('opens the dialog with instructions', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /edit roles/i }));

    expect(screen.getByText(/user roles can be edited via csv/i)).toBeInTheDocument();
    expect(screen.getByText('Row format:', { exact: false })).toBeInTheDocument();
    expect(screen.getByText(/user management role/i)).toBeInTheDocument();
    expect(screen.getByText('Example', { exact: false })).toBeInTheDocument();
  });

  it('pre-fills the textarea with current roles', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /edit roles/i }));

    const textarea = screen.getByRole('textbox');
    expect(textarea.value).toBe('andi,ADMIN,ADMIN,ADMIN,ADMIN\nbudi,,ADMIN,,');
  });

  it('submits roles', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /edit roles/i }));

    nockJophiel()
      .put('/user-roles', {
        andi: { jophiel: 'ADMIN', sandalphon: 'ADMIN', uriel: 'ADMIN', jerahmeel: 'ADMIN' },
        budi: { sandalphon: 'ADMIN' },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /submit/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
