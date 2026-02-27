import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { CourseEditDialog } from './CourseEditDialog';

const course = {
  id: 1,
  jid: 'courseJid',
  slug: 'course',
  name: 'Course',
  description: 'This is a course',
};

describe('CourseEditDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  test('edit dialog form', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <CourseEditDialog isOpen={true} course={course} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('course');
    await user.clear(slug);
    await user.type(slug, 'new-course');

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Course');
    await user.clear(name);
    await user.type(name, 'New course');

    const description = screen.getByRole('textbox', { name: /description/i });
    expect(description).toHaveValue('This is a course');
    await user.clear(description);
    await user.type(description, 'New description');

    nockJerahmeel()
      .post('/courses/courseJid', {
        slug: 'new-course',
        name: 'New course',
        description: 'New description',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
