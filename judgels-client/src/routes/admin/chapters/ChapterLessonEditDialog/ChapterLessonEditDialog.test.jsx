import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ChapterLessonEditDialog } from './ChapterLessonEditDialog';

const chapter = {
  jid: 'chapter-jid',
};

describe('ChapterLessonEditDialog', () => {
  const lessons = [
    { alias: 'A', lessonJid: 'jid-1' },
    { alias: 'B', lessonJid: 'jid-2' },
  ];

  const lessonsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
  };

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit lessons dialog form', async () => {
    nockJerahmeel().get('/chapters/chapter-jid/lessons').reply(200, { data: lessons, lessonsMap });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ChapterLessonEditDialog isOpen={true} chapter={chapter} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const editButton = await screen.findByRole('button', { name: /edit/i });
    await user.click(editButton);

    const lessonsField = screen.getByRole('textbox', { name: /lessons/i });
    expect(lessonsField).toHaveValue('A,slug-1\nB,slug-2');

    await user.clear(lessonsField);
    await user.type(lessonsField, 'P, slug-3\n  Q,slug-4  ');

    nockJerahmeel()
      .put('/chapters/chapter-jid/lessons', [
        { alias: 'P', slug: 'slug-3' },
        { alias: 'Q', slug: 'slug-4' },
      ])
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
