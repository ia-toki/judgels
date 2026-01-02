import { act, render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import courseReducer from '../modules/courseReducer';
import CoursesPage from './CoursesPage';

import * as courseActions from '../modules/courseActions';

vi.mock('../modules/courseActions');

describe('CoursesPage', () => {
  let courses;

  const renderComponent = async () => {
    courseActions.getCourses.mockReturnValue(() =>
      Promise.resolve({
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
      })
    );

    const store = createStore(
      combineReducers({ jerahmeel: combineReducers({ course: courseReducer }) }),
      applyMiddleware(thunk)
    );

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter initialEntries={['/courses']}>
            <CoursesPage />
          </TestRouter>
        </Provider>
      )
    );
  };

  describe('when there are no courses', () => {
    beforeEach(async () => {
      courses = [];
      await renderComponent();
    });

    it('shows placeholder text and no courses', () => {
      expect(screen.getByText(/no courses/i)).toBeInTheDocument();
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

    it('shows the courses', () => {
      const links = screen.getAllByRole('link');
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
