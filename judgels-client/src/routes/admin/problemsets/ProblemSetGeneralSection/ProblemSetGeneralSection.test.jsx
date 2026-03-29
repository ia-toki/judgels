import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ProblemSetGeneralSection } from './ProblemSetGeneralSection';

describe('ProblemSetGeneralSection', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const problemSet = {
    jid: 'JIDPROBLEMSET1',
    slug: 'problemset-1',
    name: 'Problemset 1',
    archiveJid: 'JIDARCHIVE1',
    description: 'Description 1',
    contestTime: 1609459200000,
  };

  const archiveSlug = 'archive-1';

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ProblemSetGeneralSection problemSet={problemSet} archiveSlug={archiveSlug} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders general details', async () => {
    await renderComponent();

    const table = screen.getByRole('table');
    expect(
      screen
        .getAllByRole('row')
        .map(row => screen.getAllByRole('cell', { container: row }).map(cell => cell.textContent))
    );
  });

  test('form', async () => {
    await renderComponent();

    const u = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await u.click(button);

    const slug = screen.getByRole('textbox', { name: /^slug/i });
    expect(slug).toHaveValue('problemset-1');
    await u.clear(slug);
    await u.type(slug, 'new-problemset');

    const name = screen.getByRole('textbox', { name: /^name/i });
    expect(name).toHaveValue('Problemset 1');
    await u.clear(name);
    await u.type(name, 'New Problemset');

    const archive = screen.getByRole('textbox', { name: /archive slug/i });
    expect(archive).toHaveValue('archive-1');
    await u.clear(archive);
    await u.type(archive, 'new-archive');

    nockJerahmeel()
      .post('/problemsets/JIDPROBLEMSET1', body => {
        return body.slug === 'new-problemset' && body.name === 'New Problemset' && body.archiveSlug === 'new-archive';
      })
      .reply(200);

    await u.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
