import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestOverviewPage from './ContestOverviewPage';

import * as contestContestantActions from '../../modules/contestContestantActions';

vi.mock('../../modules/contestContestantActions');

describe('ContestOverviewPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel().get('/contests/contestJid/description').reply(200, {
      description: 'Contest description',
    });

    contestContestantActions.getMyContestantState.mockReturnValue(Promise.resolve('NONE'));
    contestContestantActions.getApprovedContestantsCount.mockReturnValue(Promise.resolve(0));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestOverviewPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('description', () => {
    beforeEach(async () => {
      await renderComponent();
    });

    it('shows the description', async () => {
      await screen.findByText('Contest description');
    });
  });
});
