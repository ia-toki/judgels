import { act, render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import sessionReducer, { PutToken, PutUser } from '../../../../../../modules/session/sessionReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestRegistrationCard from './ContestRegistrationCard';

import * as contestContestantActions from '../../modules/contestContestantActions';
import * as contestWebActions from '../../modules/contestWebActions';

jest.mock('../../modules/contestWebActions');
jest.mock('../../modules/contestContestantActions');

describe('ContestRegistrationCard', () => {
  const renderComponent = async () => {
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
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <ContestRegistrationCard />
        </Provider>
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

    it(`shows correct texts when contestant state is ${contestantState}`, () => {
      const container = document.body;
      const stateElements = container.querySelectorAll('span.contest-registration-card__state');
      const actionButtons = container.querySelectorAll('button.contest-registration-card__action');

      expect([...stateElements].map(el => el.textContent)).toEqual(stateText);
      expect([...actionButtons].map(el => el.textContent)).toEqual(actionText);
    });
  });
});
