import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { parseDateTime } from '../../../../../../utils/datetime';
import { parseDuration } from '../../../../../../utils/duration';
import { nockUriel } from '../../../../../../utils/nock';
import contestReducer from '../../../modules/contestReducer';
import ContestEditGeneralTab from './ContestEditGeneralTab';

import * as contestActions from '../../../modules/contestActions';

vi.mock('../../../modules/contestActions');

describe('ContestEditGeneralTab', () => {
  beforeEach(async () => {
    nockUriel()
      .get('/contests/slug/contest-a')
      .reply(200, {
        jid: 'contestJid',
        slug: 'contest-a',
        name: 'Contest A',
        style: ContestStyle.ICPC,
        beginTime: parseDateTime('2018-09-10 13:00').getTime(),
      });
    nockUriel().get('/contests/slug/contest-a/config').reply(200, {});

    contestActions.updateContest.mockReturnValue(() => Promise.resolve({}));

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
            <TestRouter initialEntries={['/contests/contest-a']} path="/contests/$contestSlug">
              <ContestEditGeneralTab />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const slug = document.querySelector('input[name="slug"]');
    expect(slug).toHaveValue('contest-a');
    await user.clear(slug);
    await user.type(slug, 'contest-b');

    const name = document.querySelector('input[name="name"]');
    expect(name).toHaveValue('Contest A');
    await user.clear(name);
    await user.type(name, 'Contest B');

    const beginTime = document.querySelector('input[name="beginTime"]');
    await user.clear(beginTime);
    await user.type(beginTime, '2018-09-10 17:00');

    const duration = document.querySelector('input[name="duration"]');
    await user.clear(duration);
    await user.type(duration, '6h');

    await user.click(screen.getByRole('button', { name: /save/i }));

    expect(contestActions.updateContest).toHaveBeenCalledWith('contestJid', 'contest-a', {
      slug: 'contest-b',
      name: 'Contest B',
      style: ContestStyle.ICPC,
      beginTime: parseDateTime('2018-09-10 17:00').getTime(),
      duration: parseDuration('6h'),
    });
  });
});
