import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestRegistrantsDialog from './ContestRegistrantsDialog';

describe('ContestRegistrantsDialog', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });

    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/contestants/approved')
      .reply(200, {
        data: ['userJid1', 'userJid2', 'userJid3', 'userJid4', 'userJid5', 'userJid6'],
        profilesMap: {
          userJid1: { country: 'TH', username: 'username1', rating: { publicRating: 2000 } },
          userJid2: { country: 'ID', username: 'username2', rating: { publicRating: 1000 } },
          userJid3: { country: 'ID', username: 'username3', rating: { publicRating: 3000 } },
          userJid4: { country: 'ID', username: 'username4', rating: { publicRating: 2000 } },
          userJid5: { country: 'ID', username: 'username5', rating: { publicRating: 1000 } },
          userJid6: { username: 'username6' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestRegistrantsDialog />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  afterEach(() => {
    nock.cleanAll();
  });

  test('table', async () => {
    const rows = await screen.findAllByRole('row');
    expect(rows.slice(1).map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['Indonesia', 'username3'],
      ['Indonesia', 'username4'],
      ['Thailand', 'username1'],
      ['Indonesia', 'username2'],
      ['Indonesia', 'username5'],
      ['', 'username6'],
    ]);
  });
});
