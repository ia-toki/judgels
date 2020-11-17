import { mount } from 'enzyme';
import { createMemoryHistory, MemoryHistory } from 'history';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, createStore, combineReducers } from 'redux';
import { connectRouter, routerMiddleware, ConnectedRouter } from 'connected-react-router';
import thunk from 'redux-thunk';

import { courseReducer, PutCourse } from '../../../modules/courseReducer';
import { courseChapterReducer } from '../modules/courseChapterReducer';
import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';
import * as courseChapterActions from '../modules/courseChapterActions';
import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

jest.mock('../modules/courseChapterActions');
jest.mock('../../../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleCourseChapterDataRoute', () => {
  let history: MemoryHistory;

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store: any = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    store.dispatch(PutCourse.create({ id: 1, jid: 'jid123', slug: 'basic', name: 'Basic' }));

    mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    (courseChapterActions.getChapter as jest.Mock).mockReturnValue(() =>
      Promise.resolve({ jid: 'jid456', name: 'Chapter 123' })
    );
    (courseChapterActions.clearChapter as jest.Mock).mockReturnValue({ type: 'clear' });

    (breadcrumbsActions.pushBreadcrumb as jest.Mock).mockReturnValue({ type: 'push' });
    (breadcrumbsActions.popBreadcrumb as jest.Mock).mockReturnValue({ type: 'pop' });
  });

  test('navigation', async () => {
    render('/courses/basic/chapters/A');
    await new Promise(resolve => setImmediate(resolve));
    expect(courseChapterActions.getChapter).toHaveBeenCalledWith('jid123', 'basic', 'A');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/A', 'A. Chapter 123');

    history.push('/courses/basic/chapters/A/');
    await new Promise(resolve => setImmediate(resolve));

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/A/');
    expect(courseChapterActions.clearChapter).toHaveBeenCalled();
  });
});
