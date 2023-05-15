import { mount } from 'enzyme';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, createStore, combineReducers } from 'redux';
import { connectRouter, routerMiddleware, ConnectedRouter } from 'connected-react-router';
import thunk from 'redux-thunk';

import contestReducer from '../modules/contestReducer';
import SingleContestDataRoute from './SingleContestDataRoute';
import * as contestActions from '../modules/contestActions';
import * as contestWebActions from './modules/contestWebActions';
import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';

jest.mock('../modules/contestActions');
jest.mock('./modules/contestWebActions');
jest.mock('../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleContestDataRoute', () => {
  let history;

  const render = currentPath => {
    history = createMemoryHistory({ initialEntries: [currentPath] });

    const store = createStore(
      combineReducers({
        uriel: combineReducers({ contest: contestReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
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
    contestActions.clearContest.mockReturnValue({ type: 'clear' });
    contestWebActions.getContestBySlugWithWebConfig.mockReturnValue(() =>
      Promise.resolve({ contest: { jid: 'jid123', name: 'Contest 123' } })
    );
    contestWebActions.clearWebConfig.mockReturnValue({ type: 'clear' });
    breadcrumbsActions.pushBreadcrumb.mockReturnValue({ type: 'push' });
    breadcrumbsActions.popBreadcrumb.mockReturnValue({ type: 'pop' });
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
