import { mount } from 'enzyme';
import { MemoryHistory } from 'history';
import createMemoryHistory from 'history/createMemoryHistory';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { ConnectedRouter } from 'react-router-redux';
import createMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { createSingleContestDataRoute } from './SingleContestDataRoute';

describe('SingleContestDataRoute', () => {
  let history: MemoryHistory;
  let contestActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;

  const store = createMockStore([thunk])({});

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const SingleContestDataRoute = createSingleContestDataRoute(contestActions, breadcrumbsActions);
    mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/competition/contests/:contestId" component={SingleContestDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    contestActions = {
      fetchById: jest
        .fn()
        .mockReturnValueOnce(() => Promise.resolve({ name: 'Contest 123' }))
        .mockReturnValueOnce(() => Promise.resolve({ name: 'Contest 456' })),
      clear: jest.fn().mockReturnValue({ type: 'clear' }),
    };
    breadcrumbsActions = {
      push: jest.fn().mockReturnValue({ type: 'push' }),
      pop: jest.fn().mockReturnValue({ type: 'pop' }),
    };
  });

  test('navigation', async () => {
    render('/competition/contests/123');
    await new Promise(resolve => setImmediate(resolve));
    expect(contestActions.fetchById).toHaveBeenCalledWith(123);
    expect(breadcrumbsActions.push).toHaveBeenCalledWith('/competition/contests/123', 'Contest 123');

    history.push('/competition/contests/123/');
    await new Promise(resolve => setImmediate(resolve));

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.pop).toHaveBeenCalledWith('/competition/contests/123');
    expect(contestActions.clear).toHaveBeenCalled();
  });
});
