import { mount } from 'enzyme';
import { createMemoryHistory, MemoryHistory } from 'history';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, createStore, combineReducers } from 'redux';
import { connectRouter, routerMiddleware, ConnectedRouter } from 'connected-react-router';
import thunk from 'redux-thunk';

import { contestReducer } from '../modules/contestReducer';
import { createSingleContestDataRoute } from './SingleContestDataRoute';

describe('SingleContestDataRoute', () => {
  let history: MemoryHistory;
  let contestActions: jest.Mocked<any>;
  let contestWebActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store: any = createStore(
      combineReducers({
        uriel: combineReducers({ contest: contestReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    const SingleContestDataRoute = createSingleContestDataRoute(contestActions, contestWebActions, breadcrumbsActions);
    mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    contestActions = {
      clearContest: jest.fn().mockReturnValue({ type: 'clear' }),
    };
    contestWebActions = {
      getContestBySlugWithWebConfig: jest
        .fn()
        .mockReturnValue(() => Promise.resolve({ contest: { jid: 'jid123', name: 'Contest 123' } })),
      clearWebConfig: jest.fn().mockReturnValue({ type: 'clear' }),
    };

    breadcrumbsActions = {
      pushBreadcrumb: jest.fn().mockReturnValue({ type: 'push' }),
      popBreadcrumb: jest.fn().mockReturnValue({ type: 'pop' }),
    };
  });

  test('navigation', async () => {
    render('/contests/ioi');
    await new Promise(resolve => setImmediate(resolve));
    expect(contestWebActions.getContestBySlugWithWebConfig).toHaveBeenCalledWith('ioi');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/contests/ioi', 'Contest 123');

    history.push('/contests/ioi/');
    await new Promise(resolve => setImmediate(resolve));

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/contests/ioi/');
    expect(contestActions.clearContest).toHaveBeenCalled();
    expect(contestWebActions.clearWebConfig).toHaveBeenCalled();
  });
});
