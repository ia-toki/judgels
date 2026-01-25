import { act, render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestOverviewPage from './ContestOverviewPage';

import * as contestActions from '../../../modules/contestActions';

vi.mock('../../../modules/contestActions');

describe('ContestOverviewPage', () => {
  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestActions.getContestDescription.mockReturnValue(() =>
      Promise.resolve({
        description: 'Contest description',
      })
    );

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid', token: 'token' }));
    store.dispatch(PutContest({ jid: 'contestJid', slug: 'contest-slug' }));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
              <ContestOverviewPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('description', () => {
    beforeEach(async () => {
      await renderComponent();
    });

    it('shows the description', async () => {
      await screen.findByText('Contest description');
    });
  });
});
