import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditModulesTab from './ContestEditModulesTab';

describe('ContestEditModulesTab', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel().get('/contests/contestJid/modules').reply(200, ['REGISTRATION', 'CLARIFICATION', 'FILE']);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestEditModulesTab />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('tab', async () => {
    const modules = await screen.findAllByRole('heading', { level: 5 });
    expect(modules).toHaveLength(12);

    expect(modules.map(h5 => h5.textContent)).toEqual([
      'Registration',
      'Clarification',
      'File',
      'Clarification time limit',
      'Division',
      'Editorial',
      'Freezable scoreboard',
      'Merged scoreboard',
      'External scoreboard',
      'Virtual contest',
      'Paused',
      'Hidden',
    ]);
  });
});
