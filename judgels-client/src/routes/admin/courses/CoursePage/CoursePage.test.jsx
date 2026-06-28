import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import CoursePage from './CoursePage';

describe('CoursePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().get('/courses/slug/course-1').reply(200, {
      id: 1,
      jid: 'JIDCOURSE1',
      slug: 'course-1',
      name: 'Course 1',
      description: 'Description 1',
    });

    nockApi()
      .get('/courses/JIDCOURSE1/chapters')
      .reply(200, {
        data: [{ alias: 'A', chapterJid: 'JIDCHAPTER1' }],
        chaptersMap: { JIDCHAPTER1: { name: 'Chapter 1' } },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/courses/course-1']} path="/admin/courses/$courseSlug">
            <CoursePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await screen.findByText(/Course 1/);

    const tables = screen.getAllByRole('table');

    expect(
      within(tables[0])
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([
      ['Slug', 'course-1'],
      ['Name', 'Course 1'],
      ['Description', 'Description 1'],
    ]);

    expect(
      within(tables[1])
        .getAllByRole('row')
        .slice(1)
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([['A', 'Chapter 1']]);
  });

  test('general form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[0]);

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

    nockApi()
      .post('/courses/JIDCOURSE1', {
        slug: 'new-course',
        name: 'New Course',
        description: 'New Description',
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('chapters form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[1]);

    const chapters = document.querySelector('textarea[name="chapters"]');
    expect(chapters).toHaveValue('A,JIDCHAPTER1');
    await user.clear(chapters);
    await user.type(chapters, 'A,JIDCHAPTER2');

    nockApi()
      .put('/courses/JIDCOURSE1/chapters', [{ alias: 'A', chapterJid: 'JIDCHAPTER2' }])
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
