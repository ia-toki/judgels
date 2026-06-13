import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import { RoleEditDialog } from './RoleEditDialog';

describe('RoleEditDialog', () => {
  const currentData = {
    data: [
      { userJid: 'userJid1', role: { account: 'ADMIN', problem: 'ADMIN', contest: 'ADMIN', training: 'ADMIN' } },
      { userJid: 'userJid2', role: { problem: 'ADMIN' } },
    ],
    profilesMap: {
      userJid1: { username: 'andi' },
      userJid2: { username: 'budi' },
    },
  };

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

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

  test('form', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /edit roles/i }));

    const textarea = screen.getByRole('textbox');
    expect(textarea.value).toBe('andi,ADMIN,ADMIN,ADMIN,ADMIN\nbudi,,ADMIN,,');

    await user.clear(textarea);
    await user.type(textarea, 'andi,ADMIN,ADMIN,,\ncaca,,ADMIN,,');

    nockApi()
      .put('/user-roles', {
        andi: { account: 'ADMIN', problem: 'ADMIN' },
        caca: { problem: 'ADMIN' },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /submit/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
