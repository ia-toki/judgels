import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import CoursesPage from './CoursesPage';
import courseReducer from '../modules/courseReducer';
import * as courseActions from '../modules/courseActions';

jest.mock('../modules/courseActions');

describe('CoursesPage', () => {
  let wrapper;
  let courses;

  const render = async () => {
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

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/courses']}>
          <Route path="/courses" component={CoursesPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no courses', () => {
    beforeEach(async () => {
      courses = [];
      await render();
    });

    it('shows placeholder text and no courses', () => {
      expect(wrapper.text()).toContain('No courses.');
      expect(wrapper.find('div.content-card')).toHaveLength(0);
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
      await render();
    });

    it('shows the courses', () => {
      const cards = wrapper.find('div.content-card');
      expect(
        cards.map(card => [card.find('h4').text(), card.find('a').props().href, card.find('.html-text').text()])
      ).toEqual([
        ['Course 12 / 6 problems completed', '/courses/course-1', 'This is course 1'],
        ['Course 2', '/courses/course-2', 'This is course 2'],
      ]);
    });
  });
});
