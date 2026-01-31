import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import contestReducer from '../../../modules/contestReducer';
import ContestEditModulesTab from './ContestEditModulesTab';

import * as contestModuleActions from '../../modules/contestModuleActions';
import * as contestWebActions from '../../modules/contestWebActions';

vi.mock('../../modules/contestModuleActions');
vi.mock('../../modules/contestWebActions');

describe('ContestEditModulesTab', () => {
  beforeEach(async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestWebActions.getContestByJidWithWebConfig.mockReturnValue(() => Promise.resolve());
    contestModuleActions.getModules.mockReturnValue(() => Promise.resolve(['REGISTRATION', 'CLARIFICATION', 'FILE']));

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
              <ContestEditModulesTab />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('tab', async () => {
    const modules = await screen.findAllByRole('heading', { level: 5 });
    expect(modules).toHaveLength(12);

    expect(modules.map(h5 => h5.textContent)).toEqual([
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
