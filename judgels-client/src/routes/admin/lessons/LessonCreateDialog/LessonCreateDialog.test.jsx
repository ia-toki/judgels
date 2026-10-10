import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import { LessonCreateDialog } from './LessonCreateDialog';

describe('LessonCreateDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const openDialog = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <LessonCreateDialog />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /new lesson/i }));
    return user;
  };

  test('create dialog form', async () => {
    const user = await openDialog();

    await user.type(screen.getByRole('textbox', { name: /slug/i }), 'new-lesson');
    await user.type(screen.getByRole('textbox', { name: /additional note/i }), 'A note');

    nockApi()
      .post('/v4/lessons', {
        slug: 'new-lesson',
        additionalNote: 'A note',
        initialLanguage: 'en-US',
      })
      .reply(200, { jid: 'JIDLESS1', slug: 'new-lesson' });

    await user.click(screen.getByRole('button', { name: /create/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows an error when the slug already exists', async () => {
    const user = await openDialog();

    await user.type(screen.getByRole('textbox', { name: /slug/i }), 'old-lesson');

    nockApi()
      .post('/v4/lessons', {
        slug: 'old-lesson',
        additionalNote: '',
        initialLanguage: 'en-US',
      })
      .reply(400, { message: 'LessonSlugAlreadyExists' });

    await user.click(screen.getByRole('button', { name: /create/i }));

    expect(await screen.findByText(/slug already exists/i)).toBeInTheDocument();
  });
});
