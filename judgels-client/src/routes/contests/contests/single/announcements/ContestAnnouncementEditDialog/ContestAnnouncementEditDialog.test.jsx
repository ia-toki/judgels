import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestAnnouncementEditDialog } from './ContestAnnouncementEditDialog';

describe('ContestAnnouncementEditDialog', () => {
  const announcement = {
    jid: 'announcementJid123',
    title: 'Snack',
    content: 'Snack is provided.',
    status: 'PUBLISHED',
  };

  beforeEach(async () => {
    const onToggleEditDialog = () => {
      return;
    };

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ContestAnnouncementEditDialog
              contest={{ jid: 'contestJid' }}
              announcement={announcement}
              onToggleEditDialog={onToggleEditDialog}
            />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = screen.getByRole('button', { name: /status/i });
    // await user.click(status);

    const title = screen.getByRole('textbox', { name: /title/i });
    expect(title).toHaveValue('Snack');
    await user.clear(title);
    await user.type(title, 'Snack edited');

    const content = screen.getByRole('textbox', { name: /content/i });
    expect(content).toHaveValue('Snack is provided.');
    await user.clear(content);
    await user.type(content, 'Snack is NOT provided.');

    nockUriel()
      .put('/contests/contestJid/announcements/announcementJid123', {
        title: 'Snack edited',
        content: 'Snack is NOT provided.',
        status: 'PUBLISHED',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
