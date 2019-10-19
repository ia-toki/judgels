import { mount } from 'enzyme';
import { createMemoryHistory, MemoryHistory } from 'history';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, createStore, combineReducers } from 'redux';
import { connectRouter, routerMiddleware, ConnectedRouter } from 'connected-react-router';
import thunk from 'redux-thunk';

import { courseReducer } from '../modules/courseReducer';
import { createSingleCourseDataRoute } from './SingleCourseDataRoute';

describe('SingleCourseDataRoute', () => {
  let history: MemoryHistory;
  let courseActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store: any = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    const SingleCourseDataRoute = createSingleCourseDataRoute(courseActions, breadcrumbsActions);
    mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/courses/:courseSlug" component={SingleCourseDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    courseActions = {
      getCourseBySlug: jest.fn().mockReturnValue(() => Promise.resolve({ jid: 'jid123', name: 'Course 123' })),
      clearCourse: jest.fn().mockReturnValue({ type: 'clear' }),
    };

    breadcrumbsActions = {
      pushBreadcrumb: jest.fn().mockReturnValue({ type: 'push' }),
      popBreadcrumb: jest.fn().mockReturnValue({ type: 'pop' }),
    };
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
