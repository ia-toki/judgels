import { act, render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import courseReducer, { PutCourse } from '../../../modules/courseReducer';
import courseChapterReducer from '../modules/courseChapterReducer';
import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseChapterActions from '../modules/courseChapterActions';

vi.mock('../modules/courseChapterActions');
vi.mock('../../../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleCourseChapterDataRoute', () => {
  const renderComponent = currentPath => {
    const store = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
      }),
      applyMiddleware(thunk)
    );

    store.dispatch(PutCourse({ id: 1, jid: 'jid123', slug: 'basic', name: 'Basic' }));

    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={[currentPath]}>
          <Routes>
            <Route path="/courses/:courseSlug/chapters/:chapterAlias" element={<SingleCourseChapterDataRoute />} />
          </Routes>
        </MemoryRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    courseChapterActions.getChapter.mockReturnValue(() => Promise.resolve({ jid: 'jid456', name: 'Chapter 123' }));
    courseChapterActions.clearChapter.mockReturnValue({ type: 'clear' });
    breadcrumbsActions.pushBreadcrumb.mockReturnValue({ type: 'push' });
    breadcrumbsActions.popBreadcrumb.mockReturnValue({ type: 'pop' });
  });

  test('navigation', async () => {
    await act(async () => {
      renderComponent('/courses/basic/chapters/A');
    });

    expect(courseChapterActions.getChapter).toHaveBeenCalledWith('jid123', 'basic', 'A');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/A', 'A. Chapter 123');
  });
});
