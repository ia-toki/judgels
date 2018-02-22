import { mount } from 'enzyme';
import { MemoryHistory } from 'history';
import createMemoryHistory from 'history/createMemoryHistory';
import * as React from 'react';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux';
import createMockStore from 'redux-mock-store';

import { createSingleContestDataRoute } from './SingleContestDataRoute';
import { Route } from 'react-router';

describe('SingleContestDataRoute', () => {
  let history: MemoryHistory;
  let contestActions: jest.Mocked<any>;

  const store = createMockStore()({});

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const SingleContestDataRoute = createSingleContestDataRoute(contestActions);
    mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/competition/contests/:contestJid" component={SingleContestDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    contestActions = {
      fetch: jest.fn().mockReturnValue({ type: 'fetch' }),
      clear: jest.fn().mockReturnValue({ type: 'clear' }),
    };
  });

  test('navigation', () => {
    render('/competition/contests/JIDCONT123');
    expect(contestActions.fetch).toHaveBeenCalledWith('JIDCONT123');

    history.push('/competition/contests/JIDCONT456');
    expect(contestActions.fetch).toHaveBeenCalledWith('JIDCONT456');

    history.push('/other');
    expect(contestActions.clear).toHaveBeenCalled();
  });
});
