import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { setSession } from '../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../utils/nock';
import ContestRegistrantsButton from './ContestRegistrantsButton';

describe('ContestRegistrantsButton', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().get('/v2/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });
    nockApi().get('/v2/contests/contestJid/contestants/approved/count').reply(200, 1);
    nockApi()
      .get('/v2/contests/contestJid/contestants/approved')
      .reply(200, {
        data: ['userJid1'],
        profilesMap: { userJid1: { country: 'ID', username: 'username1' } },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestRegistrantsButton />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('opens the registrants dialog', async () => {
    const user = userEvent.setup();
    await renderComponent();

    await user.click(await screen.findByRole('button', { name: 'View registrants (1)' }));

    expect(await screen.findByText('username1')).toBeInTheDocument();
  });
});
