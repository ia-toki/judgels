import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import CourseCreatePage from './CourseCreatePage';

describe('CourseCreatePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('creates a course', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/courses/-/new']}>
            <CourseCreatePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    await user.type(slug, 'new-course');

    const name = screen.getByRole('textbox', { name: /name/i });
    await user.type(name, 'New course');

    const description = screen.getByRole('textbox', { name: /description/i });
    await user.type(description, 'New description');

    nockApi()
      .post('/v2/courses', {
        slug: 'new-course',
        name: 'New course',
        description: 'New description',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
