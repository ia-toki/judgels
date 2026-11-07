import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../../utils/nock';

import ContestRegistrationActions from './ContestRegistrationActions';

describe('ContestRegistrationActions', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ contestantState }) => {
    nockApi().get('/v2/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });
    nockApi().get('/v2/contests/contestJid/contestants/me/state').reply(200, JSON.stringify(contestantState));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestRegistrationActions />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await waitFor(() => expect(nock.isDone()).toBe(true));
  };

  const shownTexts = () => [...document.body.querySelectorAll('.bp6-tag, button')].map(el => el.textContent);

  test.each`
    contestantState                 | texts
    ${'NONE'}                       | ${[]}
    ${'REGISTRABLE_WRONG_DIVISION'} | ${['Your rating is not allowed for this contest division']}
    ${'REGISTRABLE'}                | ${['Register']}
    ${'REGISTRANT'}                 | ${['Registered', 'Unregister']}
    ${'CONTESTANT'}                 | ${['Registered']}
  `('shows $texts when contestant state is $contestantState', async ({ contestantState, texts }) => {
    await renderComponent({ contestantState });

    await waitFor(() => expect(shownTexts()).toEqual(texts));
  });

  test('registers the user', async () => {
    const user = userEvent.setup();
    await renderComponent({ contestantState: 'REGISTRABLE' });

    const registerScope = nockApi().post('/v2/contests/contestJid/contestants/me').reply(200);
    await user.click(await screen.findByRole('button', { name: 'Register' }));

    await waitFor(() => expect(registerScope.isDone()).toBe(true));
  });

  test('unregisters the user', async () => {
    const user = userEvent.setup();
    await renderComponent({ contestantState: 'REGISTRANT' });

    const unregisterScope = nockApi().delete('/v2/contests/contestJid/contestants/me').reply(200);
    await user.click(await screen.findByRole('button', { name: 'Unregister' }));

    await waitFor(() => expect(unregisterScope.isDone()).toBe(true));
  });
});
