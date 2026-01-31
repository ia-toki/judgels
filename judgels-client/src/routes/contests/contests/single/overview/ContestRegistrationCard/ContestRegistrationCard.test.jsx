import { act, render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import sessionReducer, { PutToken, PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import contestReducer from '../../../modules/contestReducer';
import ContestRegistrationCard from './ContestRegistrationCard';

import * as contestContestantActions from '../../modules/contestContestantActions';
import * as contestWebActions from '../../modules/contestWebActions';

vi.mock('../../modules/contestWebActions');
vi.mock('../../modules/contestContestantActions');

describe('ContestRegistrationCard', () => {
  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestWebActions.getWebConfig.mockReturnValue(() => Promise.resolve());
    contestContestantActions.getApprovedContestantsCount.mockReturnValue(() => Promise.resolve(10));

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));
    store.dispatch(PutToken('token'));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
              <ContestRegistrationCard />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe.each`
    contestantState                                    | stateText          | actionText
    ${ContestContestantState.RegistrableWrongDivision} | ${[]}              | ${['Your rating is not allowed for this contest division']}
    ${ContestContestantState.Registrable}              | ${[]}              | ${['Register']}
    ${ContestContestantState.Registrant}               | ${[' Registered']} | ${['Unregister']}
    ${ContestContestantState.Contestant}               | ${[' Registered']} | ${[]}
  `('text', ({ contestantState, stateText, actionText }) => {
    beforeEach(async () => {
      contestContestantActions.getMyContestantState.mockReturnValue(() => Promise.resolve(contestantState));
      await renderComponent();
    });

    it(`shows correct texts when contestant state is ${contestantState}`, async () => {
      await waitFor(() => {
        const container = document.body;
        const stateElements = container.querySelectorAll('span.contest-registration-card__state');
        const actionButtons = container.querySelectorAll('button.contest-registration-card__action');

        expect([...stateElements].map(el => el.textContent)).toEqual(stateText);
        expect([...actionButtons].map(el => el.textContent)).toEqual(actionText);
      });
    });
  });
});
