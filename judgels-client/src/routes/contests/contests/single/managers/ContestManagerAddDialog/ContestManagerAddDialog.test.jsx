import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestManagerAddDialog } from './ContestManagerAddDialog';

describe('ContestManagerAddDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ContestManagerAddDialog contest={{ jid: 'contestJid' }} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const usernames = screen.getByRole('textbox');
    await user.type(usernames, 'andi\n\nbudi\n caca  \n');

    nockUriel()
      .post('/contests/contestJid/managers/batch-upsert', ['andi', 'budi', 'caca'])
      .reply(200, { insertedManagerProfilesMap: {}, alreadyManagerProfilesMap: {} });

    const submitButton = screen.getByRole('button', { name: /^add$/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
