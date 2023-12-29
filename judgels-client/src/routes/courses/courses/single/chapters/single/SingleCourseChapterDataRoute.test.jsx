import { ConnectedRouter, connectRouter, routerMiddleware } from 'connected-react-router';
import { mount } from 'enzyme';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import courseReducer, { PutCourse } from '../../../modules/courseReducer';
import courseChapterReducer from '../modules/courseChapterReducer';
import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseChapterActions from '../modules/courseChapterActions';

jest.mock('../modules/courseChapterActions');
jest.mock('../../../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleCourseChapterDataRoute', () => {
  let history;

  const render = currentPath => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    store.dispatch(PutCourse({ id: 1, jid: 'jid123', slug: 'basic', name: 'Basic' }));

    mount(
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
    render('/courses/basic/chapters/A');
    await new Promise(resolve => setImmediate(resolve));
    expect(courseChapterActions.getChapter).toHaveBeenCalledWith('jid123', 'basic', 'A');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/A', 'A. Chapter 123');

    history.push('/courses/basic/chapters/A/');
    await new Promise(resolve => setImmediate(resolve));

    history.push('/courses/basic/chapters/B');
    await new Promise(resolve => setImmediate(resolve));
    expect(courseChapterActions.getChapter).toHaveBeenCalledWith('jid123', 'basic', 'B');
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/A');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/B', 'B. Chapter 123');

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/chapters/B');
    expect(courseChapterActions.clearChapter).toHaveBeenCalled();
  });
});
