import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestSupervisorAddDialog } from './ContestSupervisorAddDialog';

describe('ContestSupervisorAddDialog', () => {
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
            <ContestSupervisorAddDialog contest={{ jid: 'contestJid' }} />
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

    const announcementPermission = document.querySelector('input[name="managementPermissions.Announcements"]');
    await user.click(announcementPermission);

    const clarificationPermission = document.querySelector('input[name="managementPermissions.Clarifications"]');
    await user.click(clarificationPermission);

    nockUriel()
      .post('/contests/contestJid/supervisors/batch-upsert', {
        usernames: ['andi', 'budi', 'caca'],
        managementPermissions: ['ANNOUNCEMENT', 'CLARIFICATION'],
      })
      .reply(200, { upsertedSupervisorProfilesMap: {} });

    const dialog = screen.getByRole('dialog');
    const submitButton = within(dialog).getByRole('button', { name: /add\/update/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
