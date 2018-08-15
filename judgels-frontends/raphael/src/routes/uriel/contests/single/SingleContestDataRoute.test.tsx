import { mount } from 'enzyme';
import { MemoryHistory } from 'history';
import createMemoryHistory from 'history/createMemoryHistory';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { ConnectedRouter } from 'react-router-redux';
import createMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { ContestTab } from 'modules/api/uriel/contestWeb';

import { createSingleContestDataRoute } from './SingleContestDataRoute';

describe('SingleContestDataRoute', () => {
  let history: MemoryHistory;
  let contestActions: jest.Mocked<any>;
  let contestWebConfigActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;

  const store = createMockStore([thunk])({
    uriel: {
      contest: {},
    },
  });

  const render = (currentPath: string) => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const SingleContestDataRoute = createSingleContestDataRoute(
      contestActions,
      contestWebConfigActions,
      breadcrumbsActions
    );
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
      getContestBySlug: jest.fn().mockReturnValue(() => Promise.resolve({ jid: 'jid123', name: 'Contest 123' })),
      clearContest: jest.fn().mockReturnValue({ type: 'clear' }),
    };
    contestWebConfigActions = {
      getWebConfig: jest.fn().mockReturnValue(() => Promise.resolve({ visibleTabs: [ContestTab.Announcements] })),
      clearConfig: jest.fn().mockReturnValue({ type: 'clear' }),
    };

    breadcrumbsActions = {
      pushBreadcrumb: jest.fn().mockReturnValue({ type: 'push' }),
      popBreadcrumb: jest.fn().mockReturnValue({ type: 'pop' }),
    };
  });

  test('navigation', async () => {
    render('/contests/ioi');
    await new Promise(resolve => setImmediate(resolve));
    expect(contestActions.getContestBySlug).toHaveBeenCalledWith('ioi');
    expect(contestWebConfigActions.getWebConfig).toHaveBeenCalledWith('jid123');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/contests/ioi', 'Contest 123');

    history.push('/contests/ioi/');
    await new Promise(resolve => setImmediate(resolve));

    history.push('/other');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/contests/ioi');
    expect(contestActions.clearContest).toHaveBeenCalled();
    expect(contestWebConfigActions.clearConfig).toHaveBeenCalled();
  });
});
