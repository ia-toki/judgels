import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { CourseGeneralSection } from './CourseGeneralSection';

describe('CourseGeneralSection', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const course = {
    id: 1,
    jid: 'JIDCOURSE1',
    slug: 'course-1',
    name: 'Course 1',
    description: 'Description 1',
  };

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <CourseGeneralSection course={course} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders general details', async () => {
    await renderComponent();

    const table = screen.getByRole('table');
    expect(
      screen
        .getAllByRole('row')
        .map(row => screen.getAllByRole('cell', { container: row }).map(cell => cell.textContent))
    );
  });

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('course-1');
    await user.clear(slug);
    await user.type(slug, 'new-course');

    const name = screen.getAllByRole('textbox', { name: /name/i });
    expect(name[0]).toHaveValue('Course 1');
    await user.clear(name[0]);
    await user.type(name[0], 'New Course');

    const description = document.querySelector('textarea[name="description"]');
    expect(description).toHaveValue('Description 1');
    await user.clear(description);
    await user.type(description, 'New Description');

    nockJerahmeel()
      .post('/courses/JIDCOURSE1', {
        slug: 'new-course',
        name: 'New Course',
        description: 'New Description',
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
