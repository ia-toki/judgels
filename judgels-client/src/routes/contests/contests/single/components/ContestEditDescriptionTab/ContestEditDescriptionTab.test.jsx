import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditDescriptionTab from './ContestEditDescriptionTab';

describe('ContestEditDescriptionTab', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel().get('/contests/contestJid/description').reply(200, {
      description: 'current description',
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestEditDescriptionTab />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('contest edit description tab form', async () => {
    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const description = screen.getByRole('textbox');
    expect(description).toHaveValue('current description');
    await user.clear(description);
    await user.type(description, 'new description');

    nockUriel().post('/contests/contestJid/description', { description: 'new description' }).reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);
  });
});
