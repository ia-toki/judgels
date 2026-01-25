import { act, render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestManagersPage from './ContestManagersPage';

import * as contestManagerActions from '../modules/contestManagerActions';

vi.mock('../modules/contestManagerActions');

describe('ContestManagersPage', () => {
  let managers;
  let canManage;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestManagerActions.getManagers.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: managers,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        config: {
          canManage,
        },
      })
    );

    const store = createStore(combineReducers({ session: sessionReducer }), applyMiddleware(thunk));
    store.dispatch(PutUser({ jid: 'userJid', token: 'token' }));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter initialEntries={['/contests/contest-slug/managers']} path="/contests/$contestSlug/managers">
              <ContestManagersPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(() => {
      managers = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', async () => {
        await screen.findByRole('heading', { name: 'Managers' });
        expect(screen.queryByRole('button', { name: /add managers/i })).not.toBeInTheDocument();
        expect(screen.queryByRole('button', { name: /remove managers/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', async () => {
        expect(await screen.findByRole('button', { name: /add managers/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /remove managers/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no managers', () => {
      beforeEach(async () => {
        managers = [];
        await renderComponent();
      });

      it('shows placeholder text and no managers', async () => {
        expect(await screen.findByText(/no managers/i)).toBeInTheDocument();
        expect(screen.queryByRole('row')).not.toBeInTheDocument();
      });
    });

    describe('when there are managers', () => {
      beforeEach(async () => {
        managers = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await renderComponent();
      });

      it('shows the managers', async () => {
        await waitFor(() => {
          expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
        });
        const rows = screen.getAllByRole('row').slice(1);
        expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
          ['username1'],
          ['username2'],
        ]);
      });
    });
  });
});
