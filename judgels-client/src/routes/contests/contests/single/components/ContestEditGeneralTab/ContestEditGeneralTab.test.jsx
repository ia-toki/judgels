import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { parseDateTime } from '../../../../../../utils/datetime';
import { parseDuration } from '../../../../../../utils/duration';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditGeneralTab from './ContestEditGeneralTab';

describe('ContestEditGeneralTab', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });
    nockUriel()
      .get('/contests/slug/contest-a')
      .reply(200, {
        jid: 'contestJid',
        slug: 'contest-a',
        name: 'Contest A',
        style: 'ICPC',
        beginTime: parseDateTime('2018-09-10 13:00').getTime(),
      });
    nockUriel().get('/contests/slug/contest-a/config').reply(200, {});

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-a']} path="/contests/$contestSlug">
            <ContestEditGeneralTab />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const slug = document.querySelector('input[name="slug"]');
    expect(slug).toHaveValue('contest-a');
    await user.clear(slug);
    await user.type(slug, 'contest-b');

    const name = document.querySelector('input[name="name"]');
    expect(name).toHaveValue('Contest A');
    await user.clear(name);
    await user.type(name, 'Contest B');

    const beginTime = document.querySelector('input[name="beginTime"]');
    await user.clear(beginTime);
    await user.type(beginTime, '2018-09-10 17:00');

    const duration = document.querySelector('input[name="duration"]');
    await user.clear(duration);
    await user.type(duration, '6h');

    nockUriel()
      .post('/contests/contestJid', {
        slug: 'contest-b',
        name: 'Contest B',
        style: 'ICPC',
        beginTime: parseDateTime('2018-09-10 17:00').getTime(),
        duration: parseDuration('6h'),
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));
  });
});
