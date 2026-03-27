import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { parseDateTime } from '../../../../../../utils/datetime';
import { parseDuration } from '../../../../../../utils/duration';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditGeneralTab from './ContestEditGeneralTab';

describe('ContestEditGeneralTab', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockUriel()
      .get('/contests/slug/contest-a')
      .reply(200, {
        jid: 'contestJid',
        slug: 'contest-a',
        name: 'Contest A',
        style: 'ICPC',
        beginTime: parseDateTime('2018-09-10 13:00').getTime(),
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-a']} path="/contests/$contestSlug">
            <ContestEditGeneralTab />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('contest-a');
    await user.clear(slug);
    await user.type(slug, 'contest-b');

    const name = screen.getByRole('textbox', { name: /^name$/i });
    expect(name).toHaveValue('Contest A');
    await user.clear(name);
    await user.type(name, 'Contest B');

    const beginTime = document.querySelector('input[name="beginTime"]');
    await user.clear(beginTime);
    await user.type(beginTime, '2018-09-10 17:00');

    const duration = screen.getByRole('textbox', { name: /duration/i });
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

    nockUriel()
      .get('/contests/slug/contest-b')
      .reply(200, {
        jid: 'contestJid',
        slug: 'contest-b',
        name: 'Contest B',
        style: 'ICPC',
        beginTime: parseDateTime('2018-09-10 17:00').getTime(),
      });

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
