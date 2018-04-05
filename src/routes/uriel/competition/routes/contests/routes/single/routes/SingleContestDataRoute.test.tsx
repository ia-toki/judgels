import { mount } from 'enzyme';
import { MemoryHistory } from 'history';
import createMemoryHistory from 'history/createMemoryHistory';
import * as React from 'react';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux';
import createMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { createSingleContestDataRoute } from './SingleContestDataRoute';
import { Route } from 'react-router';

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
          <Route path="/beta/competition/contests/:contestJid" component={SingleContestDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    contestActions = {
      fetch: jest
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
    render('/beta/competition/contests/JIDCONT123');
    await new Promise(resolve => setImmediate(resolve));
    expect(contestActions.fetch).toHaveBeenCalledWith('JIDCONT123');
    expect(breadcrumbsActions.push).toHaveBeenCalledWith('/beta/competition/contests/JIDCONT123', 'Contest 123');

    history.push('/beta/competition/contests/JIDCONT456');
    await new Promise(resolve => setImmediate(resolve));
    expect(contestActions.fetch).toHaveBeenCalledWith('JIDCONT456');
    expect(breadcrumbsActions.pop).toHaveBeenCalledWith('/beta/competition/contests/JIDCONT123');
    expect(breadcrumbsActions.push).toHaveBeenCalledWith('/beta/competition/contests/JIDCONT456', 'Contest 456');

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.pop).toHaveBeenCalledWith('/beta/competition/contests/JIDCONT456');
    expect(contestActions.clear).toHaveBeenCalled();
  });
});
