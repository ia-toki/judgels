import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestAnnouncementCreateDialog } from './ContestAnnouncementCreateDialog';

describe('ContestAnnouncementCreateDialog', () => {
  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ContestAnnouncementCreateDialog contest={{ jid: 'contestJid' }} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = screen.getByRole('button', { name: /status/i });
    // await user.click(status);

    const title = screen.getByRole('textbox', { name: /title/i });
    await user.type(title, 'Snack');

    const content = screen.getByRole('textbox', { name: /content/i });
    await user.type(content, 'Snack is provided.');

    nockUriel()
      .post('/contests/contestJid/announcements', {
        title: 'Snack',
        content: 'Snack is provided.',
        status: 'PUBLISHED',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
