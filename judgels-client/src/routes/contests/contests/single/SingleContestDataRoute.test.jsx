import { act, render } from '@testing-library/react';
import { ConnectedRouter, connectRouter, routerMiddleware } from 'connected-react-router';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer from '../modules/contestReducer';
import SingleContestDataRoute from './SingleContestDataRoute';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestActions from '../modules/contestActions';
import * as contestWebActions from './modules/contestWebActions';

vi.mock('../modules/contestActions');
vi.mock('./modules/contestWebActions');
vi.mock('../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleContestDataRoute', () => {
  let history;

  const renderComponent = currentPath => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store = createStore(
      combineReducers({
        uriel: combineReducers({ contest: contestReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );

    render(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
        </ConnectedRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    contestActions.clearContest.mockReturnValue({ type: 'clear' });
    contestWebActions.getContestBySlugWithWebConfig.mockReturnValue(() =>
      Promise.resolve({ contest: { jid: 'jid123', name: 'Contest 123' } })
    );
    contestWebActions.clearWebConfig.mockReturnValue({ type: 'clear' });
    breadcrumbsActions.pushBreadcrumb.mockReturnValue({ type: 'push' });
    breadcrumbsActions.popBreadcrumb.mockReturnValue({ type: 'pop' });
  });

  test('navigation', async () => {
    await act(async () => {
      renderComponent('/contests/ioi');
    });

    expect(contestWebActions.getContestBySlugWithWebConfig).toHaveBeenCalledWith('ioi');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/contests/ioi', 'Contest 123');

    await act(async () => {
      history.push('/contests/ioi/');
    });

    await act(async () => {
      history.push('/other');
    });

    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith('/contests/ioi/');
    expect(contestActions.clearContest).toHaveBeenCalled();
    expect(contestWebActions.clearWebConfig).toHaveBeenCalled();
  });
});
