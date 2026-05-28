import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import CoursesPage from './CoursesPage';

describe('CoursesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    courses = [
      { jid: 'JIDCOURSE1', id: 1, slug: 'course-1', name: 'Course 1' },
      { jid: 'JIDCOURSE2', id: 2, slug: 'course-2', name: 'Course 2' },
    ],
  } = {}) => {
    nockJerahmeel().get('/courses').reply(200, { data: courses });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/courses']}>
            <CoursesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no courses', async () => {
    await renderComponent({ courses: [] });
    expect(await screen.findByText(/no courses/i)).toBeInTheDocument();
  });

  test('renders the courses table', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(
      rows.map(row =>
        within(row)
          .queryAllByRole('cell')
          .map(cell => cell.textContent)
      )
    ).toEqual([[], ['1', 'course-1', 'Course 1'], ['2', 'course-2', 'Course 2']]);
  });

  test('renders the create button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new course/i })).toBeInTheDocument();
  });
});
