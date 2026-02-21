import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestOverviewPage from './ContestOverviewPage';

describe('ContestOverviewPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel().get('/contests/contestJid/description').reply(200, {
      description: 'Contest description',
    });

    nockUriel().get('/contests/contestJid/contestants/me/state').reply(200, JSON.stringify('NONE'));
    nockUriel().get('/contests/contestJid/contestants/approved/count').reply(200, 0);

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
