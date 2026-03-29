import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import CoursePage from './CoursePage';

describe('CoursePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJerahmeel().get('/courses/slug/course-1').reply(200, {
      id: 1,
      jid: 'JIDCOURSE1',
      slug: 'course-1',
      name: 'Course 1',
      description: 'Description 1',
    });

    nockJerahmeel()
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

  test('renders course details', async () => {
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
});
