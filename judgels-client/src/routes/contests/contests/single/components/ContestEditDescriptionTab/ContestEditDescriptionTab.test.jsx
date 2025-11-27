import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestEditDescriptionTab from './ContestEditDescriptionTab';

import * as contestActions from '../../../modules/contestActions';

jest.mock('../../../modules/contestActions');

describe('ContestEditDescriptionTab', () => {
  beforeEach(async () => {
    contestActions.getContestDescription.mockReturnValue(() =>
      Promise.resolve({
        description: 'current description',
      })
    );
    contestActions.updateContestDescription.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <ContestEditDescriptionTab />
        </Provider>
      )
    );
  });

  test('contest edit description tab form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button', { name: /edit/i });
    await user.click(button);

    const description = screen.getByRole('textbox');
    expect(description).toHaveValue('current description');
    await user.clear(description);
    await user.type(description, 'new description');

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(contestActions.updateContestDescription).toHaveBeenCalledWith('contestJid', 'new description');
  });
});
