import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { CourseChapterEditDialog } from './CourseChapterEditDialog';

const course = {
  jid: 'course-jid',
};

describe('CourseChapterEditDialog', () => {
  const chapters = [
    { alias: 'A', chapterJid: 'jid-1' },
    { alias: 'B', chapterJid: 'jid-2' },
  ];

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit chapters dialog form', async () => {
    nockJerahmeel().get('/courses/course-jid/chapters').reply(200, { data: chapters, chaptersMap: {} });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <CourseChapterEditDialog isOpen={true} course={course} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const editButton = await screen.findByRole('button', { name: /edit/i });
    await user.click(editButton);

    const chaptersField = screen.getByRole('textbox', { name: /chapters/i });
    expect(chaptersField).toHaveValue('A,jid-1\nB,jid-2');

    await user.clear(chaptersField);
    await user.type(chaptersField, 'P, jid-3\n  Q,jid-4  ');

    nockJerahmeel()
      .put('/courses/course-jid/chapters', [
        { alias: 'P', chapterJid: 'jid-3' },
        { alias: 'Q', chapterJid: 'jid-4' },
      ])
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
