import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemEditorialLanguagesPage from './ProblemEditorialLanguagesPage';

describe('ProblemEditorialLanguagesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1/editorial/languages')
      .reply(200, { enabledLanguages: ['id-ID', 'en-US'], disabledLanguages: ['fr-FR'], defaultLanguage: 'id-ID' });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/editorial/languages']}
            path="/admin/problems/$problemSlug/editorial/languages"
          >
            <ProblemEditorialLanguagesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText('Indonesian (id-ID)');
  };

  const getRow = name => screen.getByRole('cell', { name }).closest('tr');

  test('table', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('table')).getAllByRole('row').slice(1);
    expect(
      rows.map(row => [
        ...[...row.querySelectorAll('td')].slice(0, 2).map(cell => cell.textContent),
        within(row)
          .queryAllByRole('button')
          .map(button => button.textContent),
      ])
    ).toEqual([
      ['English (en-US)', 'Enabled', ['Disable', 'Make default']],
      ['French (fr-FR)', 'Disabled', ['Enable']],
      ['Indonesian (id-ID)', 'Enabled (default)', []],
    ]);
  });

  test('adds a language', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: 'Add language...' }));
    expect(screen.queryByRole('option', { name: 'French (fr-FR)' })).not.toBeInTheDocument();
    await user.type(screen.getByRole('combobox', { name: 'Filter languages' }), 'japan');
    expect(screen.getAllByRole('option').map(option => option.textContent)).toEqual(['Japanese (ja-JP)']);
    await user.click(screen.getByText('Japanese (ja-JP)'));

    nockApi().post('/v4/problems/JIDPROG1/editorial/languages/ja-JP').reply(200);

    await user.click(screen.getByRole('button', { name: 'Add' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('enables, disables and makes a language default', async () => {
    await renderComponent();
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/editorial/languages/fr-FR/enable').reply(200);
    await user.click(within(getRow('French (fr-FR)')).getByRole('button', { name: 'Enable' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));

    nockApi().post('/v4/problems/JIDPROG1/editorial/languages/en-US/disable').reply(200);
    await user.click(within(getRow('English (en-US)')).getByRole('button', { name: 'Disable' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));

    nockApi().post('/v4/problems/JIDPROG1/editorial/languages/en-US/make-default').reply(200);
    await user.click(within(getRow('English (en-US)')).getByRole('button', { name: 'Make default' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
