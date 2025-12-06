import { act, render } from '@testing-library/react';
import { ConnectedRouter, connectRouter, routerMiddleware } from 'connected-react-router';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import courseReducer from '../modules/courseReducer';
import SingleCourseDataRoute from './SingleCourseDataRoute';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseActions from '../modules/courseActions';

vi.mock('../modules/courseActions');
vi.mock('../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleCourseDataRoute', () => {
  let history;

  const renderComponent = currentPath => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    render(
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
    await act(async () => {
      renderComponent('/courses/basic');
    });

    expect(courseActions.getCourseBySlug).toHaveBeenCalledWith('basic');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic', 'Course 123');

    await act(async () => {
      history.push('/courses/basic/');
    });

    await act(async () => {
      history.push('/other');
    });

    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/courses/basic/');
    expect(courseActions.clearCourse).toHaveBeenCalled();
  });
});
