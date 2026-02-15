import { act, render, screen, waitFor } from '@testing-library/react';
import { vi } from 'vitest';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestOverviewPage from './ContestOverviewPage';

import * as contestActions from '../../../modules/contestActions';
import * as contestContestantActions from '../../modules/contestContestantActions';

vi.mock('../../../modules/contestActions');
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

    contestActions.getContestDescription.mockReturnValue(
      Promise.resolve({
        description: 'Contest description',
      })
    );

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
