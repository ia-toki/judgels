import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ArchiveCreateDialog } from './ArchiveCreateDialog';

describe('ArchiveCreateDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('create dialog form', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ArchiveCreateDialog />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const slug = screen.getByRole('textbox', { name: /slug/i });
    await user.type(slug, 'new-archive');

    const name = screen.getByRole('textbox', { name: /^name$/i });
    await user.type(name, 'New archive');

    const category = screen.getByRole('textbox', { name: /category/i });
    await user.type(category, 'New category');

    const description = screen.getByRole('textbox', { name: /description/i });
    await user.type(description, 'New description');

    nockJerahmeel()
      .post('/archives', {
        slug: 'new-archive',
        name: 'New archive',
        category: 'New category',
        description: 'New description',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
