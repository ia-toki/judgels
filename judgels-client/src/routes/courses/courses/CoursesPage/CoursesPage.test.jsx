import { act, render, screen, within } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import CoursesPage from './CoursesPage';

describe('CoursesPage', () => {
  let courses;

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  const renderComponent = async () => {
    nockJerahmeel()
      .get('/courses')
      .reply(200, {
        data: courses,
        curriculum: {
          name: 'Curriculum 1',
          description: 'This is curriculum',
        },
        courseProgressesMap: {
          courseJid1: {
            solvedProblems: 2,
            totalProblems: 6,
          },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/courses']}>
            <CoursesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('when there are no courses', () => {
    beforeEach(async () => {
      courses = [];
      await renderComponent();
    });

    it('shows placeholder text and no courses', async () => {
      expect(await screen.findByText(/no courses/i)).toBeInTheDocument();
      expect(screen.queryByRole('link')).not.toBeInTheDocument();
    });
  });

  describe('when there are courses', () => {
    beforeEach(async () => {
      courses = [
        {
          jid: 'courseJid1',
          slug: 'course-1',
          name: 'Course 1',
          description: 'This is course 1',
        },
        {
          jid: 'courseJid2',
          slug: 'course-2',
          name: 'Course 2',
          description: 'This is course 2',
        },
      ];
      await renderComponent();
    });

    it('shows the courses', async () => {
      const links = await screen.findAllByRole('link');
      expect(links).toHaveLength(2);

      expect(within(links[0]).getByRole('heading', { level: 4 })).toHaveTextContent('Course 1');
      expect(within(links[0]).getByRole('heading', { level: 4 })).toHaveTextContent('2 / 6 problems completed');
      expect(links[0]).toHaveAttribute('href', '/courses/course-1');
      expect(links[0]).toHaveTextContent('This is course 1');

      expect(within(links[1]).getByRole('heading', { level: 4 })).toHaveTextContent('Course 2');
      expect(links[1]).toHaveAttribute('href', '/courses/course-2');
      expect(links[1]).toHaveTextContent('This is course 2');
    });
  });
});
