import { act, render, waitFor } from '@testing-library/react';
import { vi } from 'vitest';

import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestRegistrationCard from './ContestRegistrationCard';

import * as contestContestantActions from '../../modules/contestContestantActions';

vi.mock('../../modules/contestContestantActions');

describe('ContestRegistrationCard', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });
    nockUriel().get('/contests/slug/contest-slug/config').reply(200, {});

    contestContestantActions.getApprovedContestantsCount.mockReturnValue(Promise.resolve(10));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestRegistrationCard />
          </TestRouter>
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
      contestContestantActions.getMyContestantState.mockReturnValue(Promise.resolve(contestantState));
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
