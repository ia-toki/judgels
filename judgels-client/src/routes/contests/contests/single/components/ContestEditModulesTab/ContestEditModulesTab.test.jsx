import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../../utils/nock';
import ContestEditModulesTab from './ContestEditModulesTab';

describe('ContestEditModulesTab', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockApi().get('/contests/contestJid/modules').reply(200, ['REGISTRATION', 'CLARIFICATION', 'FILE']);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestEditModulesTab />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders modules', async () => {
    await renderComponent();

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
