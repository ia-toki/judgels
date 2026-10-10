import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemItemsPage from './ProblemItemsPage';

describe('ProblemItemsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const items = [
    { jid: 'JIDITEM1', type: 'STATEMENT', meta: 'intro', config: { statement: '<p>Read this.</p>' } },
    {
      jid: 'JIDITEM2',
      type: 'MULTIPLE_CHOICE',
      number: 1,
      meta: 'addition',
      config: {
        statement: '<p>1 + 1?</p>',
        score: 4,
        penalty: -1,
        choices: [
          { alias: 'a', content: '1', isCorrect: false },
          { alias: 'b', content: '2', isCorrect: true },
        ],
      },
    },
    {
      jid: 'JIDITEM3',
      type: 'SHORT_ANSWER',
      number: 2,
      meta: '',
      config: { statement: '<p>2 + 2?</p>', score: 2, penalty: 0, inputValidationRegex: '\\d+', gradingRegex: '4' },
    },
    { jid: 'JIDITEM4', type: 'ESSAY', number: 3, meta: '', config: { statement: '<p>Explain.</p>', score: 10 } },
  ];

  const nockItems = data => nockApi().get('/v4/problems/JIDBUND1/items').reply(200, { data });

  const renderComponent = async ({ canEdit = true, data = items } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDBUND1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDBUND1')
      .reply(200, {
        data: { id: 1, jid: 'JIDBUND1', slug: 'problem-1', type: 'BUNDLE', authorJid: 'JIDUSER1' },
        setterJidsMap: {},
        topicTags: [],
        hasLocalChanges: false,
        config: { canEdit, canManage: canEdit },
        profilesMap: {},
      });
    nockItems(data);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problems/problem-1/items']} path="/admin/problems/$problemSlug/items">
            <ProblemItemsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('heading', { name: /items/i });
  };

  const getRows = () =>
    screen
      .getAllByRole('row')
      .slice(1)
      .map(row =>
        within(row)
          .getAllByRole('cell')
          .slice(0, 6)
          .map(cell => cell.textContent)
      );

  test('lists the items', async () => {
    await renderComponent();

    expect(getRows()).toEqual([
      ['', 'Statement', 'intro', '', '', ''],
      ['1', 'Multiple Choice', 'addition', '4 (-1)', 'a b', 'b'],
      ['2', 'Short Answer', '', '2', '\\d+', '4'],
      ['3', 'Essay', '', '10', '', ''],
    ]);
    expect(screen.getByRole('link', { name: 'Multiple Choice' })).toHaveAttribute(
      'href',
      '/admin/problems/problem-1/items/JIDITEM2'
    );
  });

  test('shows a placeholder when there are no items', async () => {
    await renderComponent({ data: [] });
    expect(screen.getByText('No items.')).toBeInTheDocument();
  });

  test('is read-only for a viewer', async () => {
    await renderComponent({ canEdit: false });

    expect(screen.getByRole('link', { name: 'Essay' })).toBeInTheDocument();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
    expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
  });

  test('adds an item', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.selectOptions(screen.getByRole('combobox', { name: 'Item type' }), 'ESSAY');

    nockApi()
      .post('/v4/problems/JIDBUND1/items', { type: 'ESSAY' })
      .reply(200, { jid: 'JIDITEM5', type: 'ESSAY', number: 4, meta: '', config: { statement: '', score: 1 } });

    await user.click(screen.getByRole('button', { name: 'Add' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('moves an item', async () => {
    await renderComponent();
    const user = userEvent.setup();

    expect(screen.getByRole('button', { name: 'Move Statement item up' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Move Essay item no. 3 down' })).toBeDisabled();

    nockApi().post('/v4/problems/JIDBUND1/items/JIDITEM2/move-up').reply(200);
    await user.click(screen.getByRole('button', { name: 'Move Multiple Choice item no. 1 up' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));

    nockApi().post('/v4/problems/JIDBUND1/items/JIDITEM3/move-down').reply(200);
    await user.click(screen.getByRole('button', { name: 'Move Short Answer item no. 2 down' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('deletes an item', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: 'Delete Essay item no. 3' }));
    expect(screen.getByText('Delete this Essay item no. 3?')).toBeInTheDocument();

    nockApi().delete('/v4/problems/JIDBUND1/items/JIDITEM4').reply(200);

    await user.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
