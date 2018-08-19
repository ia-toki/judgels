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
  let contestWebActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;

  const store = createMockStore([thunk])({
    uriel: {
      contest: {},
    },
  });

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

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
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/contests/ioi');
    expect(contestActions.clearContest).toHaveBeenCalled();
    expect(contestWebActions.clearWebConfig).toHaveBeenCalled();
  });
});
