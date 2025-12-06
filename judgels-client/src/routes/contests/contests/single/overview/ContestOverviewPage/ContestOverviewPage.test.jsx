import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer from '../../../../../../modules/session/sessionReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestOverviewPage from './ContestOverviewPage';

import * as contestActions from '../../../modules/contestActions';

vi.mock('../../../modules/contestActions');

describe('ContestOverviewPage', () => {
  const renderComponent = async () => {
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
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <ContestOverviewPage />
        </Provider>
      )
    );
  };

  describe('description', () => {
    beforeEach(async () => {
      await renderComponent();
    });

    it('shows the description', () => {
      expect(screen.getByText('Contest description')).toBeInTheDocument();
    });
  });
});
