import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ChapterSubmissionsPage from './ChapterSubmissionsPage';
import sessionReducer, { PutUser } from '../../../../../../../../modules/session/sessionReducer';
import courseReducer, { PutCourse } from '../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../modules/courseChapterReducer';
import * as chapterSubmissionActions from '../modules/chapterSubmissionActions';

jest.mock('../modules/chapterSubmissionActions');

describe('ChapterSubmissionsPage', () => {
  let wrapper;
  let submissions;
  let canManage;

  const render = async () => {
    chapterSubmissionActions.getSubmissions.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: submissions,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        problemAliasesMap: {
          'chapterJid-problemJid1': 'A',
          'chapterJid-problemJid2': 'B',
        },
        config: {
          canManage,
          userJids: ['userJid1', 'userJid2'],
          problemJids: ['problemJid1', 'problemJid2'],
        },
      })
    );

    chapterSubmissionActions.getSubmissionSourceImage.mockReturnValue(() => Promise.resolve('image.url'));

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid1', username: 'username' }));
    store.dispatch(PutCourse({ jid: 'courseJid', slug: 'courseSlug' }));
    store.dispatch(
      PutCourseChapter({
        jid: 'chapterJid',
        name: 'Chapter 1',
        alias: 'chapter-1',
        courseSlug: 'courseSlug',
      })
    );

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1/submissions']}>
          <Route path="/courses/courseSlug/chapter/chapter-1/submissions" component={ChapterSubmissionsPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(() => {
      submissions = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await render();
      });

      it('shows no buttons', () => {
        expect(wrapper.find('.action-buttons').find('button')).toHaveLength(0);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await render();
      });

      it('shows action buttons', () => {
        expect(
          wrapper
            .find('.action-buttons')
            .find('button')
            .map(b => b.text())
        ).toEqual(['refreshRegrade all pages']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no submissions', () => {
      beforeEach(async () => {
        submissions = [];
        await render();
      });

      it('shows placeholder text and no submissions', () => {
        expect(wrapper.text()).toContain('No submissions.');
        expect(wrapper.find('tr')).toHaveLength(0);
      });
    });

    describe('when there are submissions', () => {
      beforeEach(() => {
        submissions = [
          {
            id: 20,
            jid: 'submissionJid1',
            containerJid: 'chapterJid',
            userJid: 'userJid1',
            problemJid: 'problemJid1',
            gradingLanguage: 'Cpp17',
            time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
            latestGrading: {
              verdict: { code: 'AC' },
              score: 100,
            },
          },
          {
            id: 10,
            jid: 'submissionJid2',
            containerJid: 'chapterJid',
            userJid: 'userJid2',
            problemJid: 'problemJid2',
            gradingLanguage: 'Cpp17',
            time: new Date(new Date().setDate(new Date().getDate() - 2)).getTime(),
          },
        ];
      });

      describe('when not canManage', () => {
        beforeEach(async () => {
          canManage = false;
          await render();
        });

        it('shows the submissions', () => {
          expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text().trim()))).toEqual([
            [],
            ['20', 'username1', 'A', 'C++17', 'AC', '100', '1 day ago', 'search'],
            ['10', 'username2', 'B', 'C++17', '', '', '2 days ago', 'search'],
          ]);
        });
      });

      describe('when canManage', () => {
        beforeEach(async () => {
          canManage = true;
          await render();
        });

        it('shows the submissions', () => {
          expect(
            wrapper.find('tr').map(tr =>
              tr.find('td').map(td =>
                td
                  .text()
                  .replace(/\s+/g, ' ')
                  .trim()
              )
            )
          ).toEqual([
            [],
            ['20 refresh', 'username1', 'A', 'C++17', 'AC', '100', '1 day ago', 'search'],
            ['10 refresh', 'username2', 'B', 'C++17', '', '', '2 days ago', 'search'],
          ]);
        });
      });
    });
  });
});
