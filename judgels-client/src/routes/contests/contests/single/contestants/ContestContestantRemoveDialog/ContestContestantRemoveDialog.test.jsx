import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestContestantRemoveDialog } from './ContestContestantRemoveDialog';

describe('ContestContestantRemoveDialog', () => {
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
            <ContestContestantRemoveDialog contest={{ jid: 'contestJid' }} />
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
      .post('/contests/contestJid/contestants/batch-delete', ['andi', 'budi', 'caca'])
      .reply(200, { deletedContestantProfilesMap: {} });

    const submitButton = screen.getByRole('button', { name: /remove$/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
