import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import CourseChaptersPage from './CourseChaptersPage';
import courseReducer, { PutCourse } from '../../../modules/courseReducer';
import courseChapterReducer from '../modules/courseChapterReducer';
import * as courseChapterActions from '../modules/courseChapterActions';

jest.mock('../modules/courseChapterActions');

describe('CourseChaptersPage', () => {
  let wrapper;
  let chapters;

  const render = async () => {
    courseChapterActions.getChapters.mockReturnValue(() =>
      Promise.resolve({
        data: chapters,
        chaptersMap: {
          chapterJid1: { name: 'Chapter 1' },
          chapterJid2: { name: 'Chapter 2' },
        },
        chapterProgressesMap: {
          chapterJid1: {
            solvedProblems: 2,
            totalProblems: 5,
          },
          chapterJid2: {
            solvedProblems: 0,
            totalProblems: 4,
          },
        },
      })
    );

    const store = createStore(
      combineReducers({ jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutCourse({ jid: 'courseJid', slug: 'courseSlug' }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/courses/courseSlug']}>
          <Route path="/courses/courseSlug" component={CourseChaptersPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no chapters', () => {
    beforeEach(async () => {
      chapters = [];
      await render();
    });

    it('shows placeholder text and no chapters', () => {
      expect(wrapper.text()).toContain('No chapters.');
      expect(wrapper.find('div.course-chapter-card')).toHaveLength(0);
    });
  });

  describe('when there are chapters', () => {
    beforeEach(async () => {
      chapters = [
        { chapterJid: 'chapterJid1', alias: '1' },
        { chapterJid: 'chapterJid2', alias: '2' },
      ];
      await render();
    });

    it('shows the chapters', () => {
      const cards = wrapper.find('div.course-chapter-card');
      expect(cards.map(card => [card.find('h4').text(), card.find('a').props().href])).toEqual([
        ['1. Chapter 12 / 5 solved', '/courses/courseSlug/chapters/1'],
        ['2. Chapter 20 / 4 solved', '/courses/courseSlug/chapters/2'],
      ]);
    });
  });
});
