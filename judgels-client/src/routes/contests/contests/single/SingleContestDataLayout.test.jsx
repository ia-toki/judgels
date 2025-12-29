import { act, render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer from '../modules/contestReducer';
import contestWebConfigReducer from '../modules/contestWebConfigReducer';
import SingleContestDataLayout from './SingleContestDataLayout';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestActions from '../modules/contestActions';
import * as contestWebActions from './modules/contestWebActions';

vi.mock('../modules/contestActions');
vi.mock('./modules/contestWebActions');
vi.mock('../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleContestDataLayout', () => {
  const renderComponent = currentPath => {
    const store = createStore(
      combineReducers({
        uriel: combineReducers({
          contest: contestReducer,
          contestWebConfig: contestWebConfigReducer,
        }),
      }),
      applyMiddleware(thunk)
    );

    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={[currentPath]}>
          <Routes>
            <Route path="/contests/:contestSlug" element={<SingleContestDataLayout />} />
          </Routes>
        </MemoryRouter>
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
  });
});
