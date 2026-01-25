import { act, render, screen, waitFor, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestSupervisorsPage from './ContestSupervisorsPage';

import * as contestSupervisorActions from '../../modules/contestSupervisorActions';

vi.mock('../../modules/contestSupervisorActions');

describe('ContestSupervisorsPage', () => {
  let supervisors;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestSupervisorActions.getSupervisors.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: supervisors,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
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
              initialEntries={['/contests/contest-slug/supervisors']}
              path="/contests/$contestSlug/supervisors"
            >
              <ContestSupervisorsPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(async () => {
      supervisors = [];
      await renderComponent();
    });

    it('shows action buttons', async () => {
      expect(await screen.findByRole('button', { name: /add\/update supervisors/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /remove supervisors/i })).toBeInTheDocument();
    });
  });

  describe('content', () => {
    describe('when there are no supervisors', () => {
      beforeEach(async () => {
        supervisors = [];
        await renderComponent();
      });

      it('shows placeholder text and no supervisors', async () => {
        expect(await screen.findByText(/no supervisors/i)).toBeInTheDocument();
        const rows = screen.queryAllByRole('row');
        expect(rows).toHaveLength(0);
      });
    });

    describe('when there are supervisors', () => {
      beforeEach(async () => {
        supervisors = [
          {
            userJid: 'userJid1',
            managementPermissions: ['ANNOUNCEMENT', 'PROBLEM'],
          },
          {
            userJid: 'userJid2',
            managementPermissions: ['ALL'],
          },
        ];
        await renderComponent();
      });

      it('shows the supervisors', async () => {
        await waitFor(() => {
          expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
        });
        const rows = screen.getAllByRole('row').slice(1);
        expect(
          rows.map(row =>
            within(row)
              .queryAllByRole('cell')
              .map(cell => cell.textContent)
          )
        ).toEqual([
          ['username1', 'ANNCPROB'],
          ['username2', 'ALL'],
        ]);
      });
    });
  });
});
