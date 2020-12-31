import { mount } from 'enzyme';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, createStore, combineReducers } from 'redux';
import { connectRouter, routerMiddleware, ConnectedRouter } from 'connected-react-router';
import thunk from 'redux-thunk';

import courseReducer from '../modules/courseReducer';
import SingleCourseDataRoute from './SingleCourseDataRoute';
import * as courseActions from '../modules/courseActions';
import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';

jest.mock('../modules/courseActions');
jest.mock('../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleCourseDataRoute', () => {
  let history;

  const render = currentPath => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/courses/:courseSlug" component={SingleCourseDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    courseActions.getCourseBySlug.mockReturnValue(() => Promise.resolve({ jid: 'jid123', name: 'Course 123' }));
    courseActions.clearCourse.mockReturnValue({ type: 'clear' });

    breadcrumbsActions.pushBreadcrumb.mockReturnValue({ type: 'push' });
    breadcrumbsActions.popBreadcrumb.mockReturnValue({ type: 'pop' });
  });

  test('navigation', async () => {
    render('/courses/basic');
    await new Promise(resolve => setImmediate(resolve));
    expect(courseActions.getCourseBySlug).toHaveBeenCalledWith('basic');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic', 'Course 123');

    history.push('/courses/basic/');
    await new Promise(resolve => setImmediate(resolve));

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/');
    expect(courseActions.clearCourse).toHaveBeenCalled();
  });
});
