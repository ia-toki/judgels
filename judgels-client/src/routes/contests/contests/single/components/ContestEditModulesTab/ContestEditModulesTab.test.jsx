import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestEditModulesTab from './ContestEditModulesTab';

import * as contestModuleActions from '../../modules/contestModuleActions';
import * as contestWebActions from '../../modules/contestWebActions';

jest.mock('../../modules/contestModuleActions');
jest.mock('../../modules/contestWebActions');

describe('ContestEditModulesTab', () => {
  beforeEach(async () => {
    contestWebActions.getContestByJidWithWebConfig.mockReturnValue(() => Promise.resolve());
    contestModuleActions.getModules.mockReturnValue(() => Promise.resolve(['REGISTRATION', 'CLARIFICATION', 'FILE']));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <ContestEditModulesTab />
        </Provider>
      )
    );
  });

  test('tab', () => {
    const modules = screen.getAllByRole('heading', { level: 5 });
    expect(modules).toHaveLength(12);

    expect(screen.getAllByRole('heading', { level: 5 }).map(h5 => h5.textContent)).toEqual([
      'Registration',
      'Clarification',
      'File',
      'Clarification time limit',
      'Division',
      'Editorial',
      'Freezable scoreboard',
      'Merged scoreboard',
      'External scoreboard',
      'Virtual contest',
      'Paused',
      'Hidden',
    ]);
  });
});
