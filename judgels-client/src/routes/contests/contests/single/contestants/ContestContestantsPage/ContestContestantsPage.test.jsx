import { act, render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestContestantsPage from './ContestContestantsPage';

import * as contestContestantActions from '../../modules/contestContestantActions';

vi.mock('../../modules/contestContestantActions');

describe('ContestContestantsPage', () => {
  let contestants;
  let canManage;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestContestantActions.getContestants.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: contestants,
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
            <TestRouter
              initialEntries={['/contests/contest-slug/contestants']}
              path="/contests/$contestSlug/contestants"
            >
              <ContestContestantsPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(() => {
      contestants = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', async () => {
        await screen.findByRole('heading', { name: 'Contestants' });
        expect(screen.queryByRole('button', { name: /add contestants/i })).not.toBeInTheDocument();
        expect(screen.queryByRole('button', { name: /remove contestants/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', async () => {
        expect(await screen.findByRole('button', { name: /add contestants/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /remove contestants/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no contestants', () => {
      beforeEach(async () => {
        contestants = [];
        await renderComponent();
      });

      it('shows placeholder text and no contestants', async () => {
        expect(await screen.findByText(/no contestants/i)).toBeInTheDocument();
        expect(screen.queryByRole('row')).not.toBeInTheDocument();
      });
    });

    describe('when there are contestants', () => {
      beforeEach(async () => {
        contestants = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await renderComponent();
      });

      it('shows the contestants', async () => {
        await waitFor(() => {
          expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
        });
        const rows = screen.getAllByRole('row');
        expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
          [],
          ['1', 'username1'],
          ['2', 'username2'],
        ]);
      });
    });
  });
});
