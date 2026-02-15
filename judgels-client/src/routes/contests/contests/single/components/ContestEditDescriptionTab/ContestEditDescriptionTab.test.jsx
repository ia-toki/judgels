import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditDescriptionTab from './ContestEditDescriptionTab';

import * as contestActions from '../../../modules/contestActions';

vi.mock('../../../modules/contestActions');

describe('ContestEditDescriptionTab', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestActions.getContestDescription.mockReturnValue(
      Promise.resolve({
        description: 'current description',
      })
    );
    contestActions.updateContestDescription.mockReturnValue(Promise.resolve({}));

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

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(contestActions.updateContestDescription).toHaveBeenCalledWith('contestJid', 'new description');
  });
});
