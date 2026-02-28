import { act, render, waitFor } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestRegistrationCard from './ContestRegistrationCard';

describe('ContestRegistrationCard', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async contestantState => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });
    nockUriel().get('/contests/slug/contest-slug/config').reply(200, {});

    nockUriel().get('/contests/contestJid/contestants/me/state').reply(200, JSON.stringify(contestantState));
    nockUriel().get('/contests/contestJid/contestants/approved/count').reply(200, 10);

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
    contestantState                 | stateText          | actionText
    ${'REGISTRABLE_WRONG_DIVISION'} | ${[]}              | ${['Your rating is not allowed for this contest division']}
    ${'REGISTRABLE'}                | ${[]}              | ${['Register']}
    ${'REGISTRANT'}                 | ${[' Registered']} | ${['Unregister']}
    ${'CONTESTANT'}                 | ${[' Registered']} | ${[]}
  `('text', ({ contestantState, stateText, actionText }) => {
    beforeEach(async () => {
      await renderComponent(contestantState);
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
