import { act, render } from '@testing-library/react';
import { ConnectedRouter, connectRouter, routerMiddleware } from 'connected-react-router';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
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
  let history;

  const renderComponent = currentPath => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    store.dispatch(PutCourse({ id: 1, jid: 'jid123', slug: 'basic', name: 'Basic' }));

    render(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
        </ConnectedRouter>
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

    await act(async () => {
      history.push('/courses/basic/chapters/B');
    });

    expect(courseChapterActions.getChapter).toHaveBeenCalledWith('jid123', 'basic', 'B');
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/A');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/B', 'B. Chapter 123');

    await act(async () => {
      history.push('/other');
    });

    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/B');
    expect(courseChapterActions.clearChapter).toHaveBeenCalled();
  });
});
