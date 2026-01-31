import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import contestReducer from '../../../modules/contestReducer';
import ContestEditDescriptionTab from './ContestEditDescriptionTab';

import * as contestActions from '../../../modules/contestActions';

vi.mock('../../../modules/contestActions');

describe('ContestEditDescriptionTab', () => {
  beforeEach(async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestActions.getContestDescription.mockReturnValue(() =>
      Promise.resolve({
        description: 'current description',
      })
    );
    contestActions.updateContestDescription.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
              <ContestEditDescriptionTab />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('contest edit description tab form', async () => {
    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
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
