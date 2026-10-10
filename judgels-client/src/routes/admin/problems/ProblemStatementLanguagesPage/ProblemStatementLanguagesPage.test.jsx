import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemStatementLanguagesPage from './ProblemStatementLanguagesPage';

describe('ProblemStatementLanguagesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
        setterJidsMap: {},
        topicTags: [],
        hasLocalChanges: false,
        config: { canEdit, canManage: canEdit },
        profilesMap: {},
      });
    nockApi()
      .get('/v4/problems/JIDPROG1/statement/languages')
      .reply(200, { enabledLanguages: ['id-ID', 'en-US'], disabledLanguages: ['fr-FR'], defaultLanguage: 'id-ID' });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/languages']}
            path="/admin/problems/$problemSlug/languages"
          >
            <ProblemStatementLanguagesPage />
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

  test('hides the actions when the problem cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
    expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
  });

  test('adds a language', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const select = screen.getByRole('combobox', { name: /add language/i });
    expect(within(select).queryByRole('option', { name: 'French (fr-FR)' })).not.toBeInTheDocument();
    await user.selectOptions(select, 'ja-JP');

    nockApi().post('/v4/problems/JIDPROG1/statement/languages/ja-JP').reply(200);

    await user.click(screen.getByRole('button', { name: 'Add' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('enables, disables and makes a language default', async () => {
    await renderComponent();
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/statement/languages/fr-FR/enable').reply(200);
    await user.click(within(getRow('French (fr-FR)')).getByRole('button', { name: 'Enable' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));

    nockApi().post('/v4/problems/JIDPROG1/statement/languages/en-US/disable').reply(200);
    await user.click(within(getRow('English (en-US)')).getByRole('button', { name: 'Disable' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));

    nockApi().post('/v4/problems/JIDPROG1/statement/languages/en-US/make-default').reply(200);
    await user.click(within(getRow('English (en-US)')).getByRole('button', { name: 'Make default' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
