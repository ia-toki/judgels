import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemItemPage from './ProblemItemPage';

describe('ProblemItemPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const statementItem = { jid: 'JIDITEM1', type: 'STATEMENT', meta: 'intro', config: { statement: 'Read this.' } };

  const multipleChoiceItem = {
    jid: 'JIDITEM1',
    type: 'MULTIPLE_CHOICE',
    number: 2,
    meta: 'addition',
    config: {
      statement: 'What is 1 + 1?',
      score: 4,
      penalty: -1,
      choices: [
        { alias: 'a', content: 'one <img src="render/one.png">', isCorrect: false },
        { alias: 'b', content: 'two', isCorrect: true },
      ],
    },
  };

  const shortAnswerItem = {
    jid: 'JIDITEM1',
    type: 'SHORT_ANSWER',
    number: 1,
    meta: '',
    config: { statement: 'What is 2 + 2?', score: 2, penalty: 0, inputValidationRegex: '\\d+', gradingRegex: '4' },
  };

  const essayItem = {
    jid: 'JIDITEM1',
    type: 'ESSAY',
    number: 1,
    meta: '',
    config: { statement: 'Explain.', score: 10 },
  };

  const renderComponent = async ({ canEdit = true, item }) => {
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
    nockApi()
      .get('/v4/problems/JIDBUND1/statement/languages')
      .reply(200, { enabledLanguages: ['id-ID', 'en-US'], disabledLanguages: [], defaultLanguage: 'id-ID' });
    nockApi().get('/v4/problems/JIDBUND1/items/JIDITEM1').query({ language: 'id-ID' }).reply(200, item);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/JIDBUND1/items/JIDITEM1']}
            path="/admin/problems/$problemJid/items/$itemJid"
          >
            <ProblemItemPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('heading', { name: /(statement|multiple choice|short answer|essay) item/i });
  };

  const nockUpdate = (language, data) =>
    nockApi().put('/v4/problems/JIDBUND1/items/JIDITEM1', data).query({ language }).reply(200);

  const save = async user => {
    await user.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(nock.isDone()).toBe(true));
  };

  test('shows the item in the default language', async () => {
    await renderComponent({ item: multipleChoiceItem });

    expect(screen.getByRole('heading', { name: /multiple choice item \(no\. 2\)/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Items' })).toHaveAttribute('href', '/admin/problems/JIDBUND1/items');
    expect(screen.getByRole('combobox', { name: 'Language' })).toHaveValue('id-ID');

    expect(await screen.findByRole('textbox', { name: 'Internal note' })).toHaveValue('addition');
    expect(screen.getByRole('textbox', { name: 'Statement' })).toHaveValue('What is 1 + 1?');
    expect(screen.getByRole('textbox', { name: /^score/i })).toHaveValue('4');
    expect(screen.getByRole('textbox', { name: /^penalty/i })).toHaveValue('-1');
    expect(screen.getByRole('textbox', { name: 'Alias of choice 2' })).toHaveValue('b');
    expect(screen.getByRole('textbox', { name: 'Content of choice 2' })).toHaveValue('two');
    expect(screen.getByRole('checkbox', { name: 'Choice 1 is correct' })).not.toBeChecked();
    expect(screen.getByRole('checkbox', { name: 'Choice 2 is correct' })).toBeChecked();
  });

  test('previews the item for a viewer', async () => {
    await renderComponent({ canEdit: false, item: multipleChoiceItem });

    expect(await screen.findByText('What is 1 + 1?')).toBeInTheDocument();
    expect(screen.getByText('two')).toBeInTheDocument();
    expect(document.querySelector('img').getAttribute('src')).toMatch(/\/v2\/problems\/JIDBUND1\/render\/one\.png$/);
    screen.getAllByRole('radio').forEach(radio => expect(radio).toBeDisabled());
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
  });

  test('switches the language', async () => {
    await renderComponent({ item: essayItem });
    const user = userEvent.setup();

    nockApi()
      .get('/v4/problems/JIDBUND1/items/JIDITEM1')
      .query({ language: 'en-US' })
      .reply(200, { ...essayItem, config: { statement: 'Explain it.', score: 10 } });

    await user.selectOptions(screen.getByRole('combobox', { name: 'Language' }), 'en-US');

    await waitFor(() => expect(screen.getByRole('textbox', { name: 'Statement' })).toHaveValue('Explain it.'));

    nockUpdate('en-US', { type: 'ESSAY', meta: '', config: { statement: 'Explain it.', score: 10 } });
    await save(user);
  });

  test('updates a statement item', async () => {
    await renderComponent({ item: statementItem });
    const user = userEvent.setup();

    expect(screen.queryByRole('textbox', { name: /^score/i })).not.toBeInTheDocument();

    const meta = await screen.findByRole('textbox', { name: 'Internal note' });
    await user.clear(meta);
    await user.type(meta, 'opening');

    const statement = screen.getByRole('textbox', { name: 'Statement' });
    await user.clear(statement);
    await user.type(statement, 'Read that.');

    nockUpdate('id-ID', { type: 'STATEMENT', meta: 'opening', config: { statement: 'Read that.' } });
    await save(user);
  });

  test('updates a multiple choice item', async () => {
    await renderComponent({ item: multipleChoiceItem });
    const user = userEvent.setup();

    const score = await screen.findByRole('textbox', { name: /^score/i });
    await user.clear(score);
    await user.type(score, '5');

    await user.click(screen.getByRole('button', { name: 'Remove choice 1' }));
    await user.click(screen.getByRole('button', { name: 'New choice' }));
    await user.type(screen.getByRole('textbox', { name: 'Content of choice 2' }), 'three');

    // a choice without an alias
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByText('Every choice must have an alias')).toBeInTheDocument();

    await user.type(screen.getByRole('textbox', { name: 'Alias of choice 2' }), 'b');
    expect(await screen.findByText('Every choice must have its own alias')).toBeInTheDocument();

    await user.clear(screen.getByRole('textbox', { name: 'Alias of choice 2' }));
    await user.type(screen.getByRole('textbox', { name: 'Alias of choice 2' }), 'c');
    await user.click(screen.getByRole('checkbox', { name: 'Choice 1 is correct' }));
    await user.click(screen.getByRole('checkbox', { name: 'Choice 2 is correct' }));

    nockUpdate('id-ID', {
      type: 'MULTIPLE_CHOICE',
      meta: 'addition',
      config: {
        statement: 'What is 1 + 1?',
        score: 5,
        penalty: -1,
        choices: [
          { alias: 'b', content: 'two', isCorrect: false },
          { alias: 'c', content: 'three', isCorrect: true },
        ],
      },
    });
    await save(user);
  });

  test('updates a short answer item', async () => {
    await renderComponent({ item: shortAnswerItem });
    const user = userEvent.setup();

    const format = await screen.findByRole('textbox', { name: /^answer format regex/i });
    await user.clear(format);
    await user.type(format, '(');

    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByText('Must be a valid regex')).toBeInTheDocument();

    await user.clear(format);
    await user.type(format, '.*');
    await user.clear(screen.getByRole('textbox', { name: /^correct answer regex/i }));

    const penalty = screen.getByRole('textbox', { name: /^penalty/i });
    await user.clear(penalty);
    await user.type(penalty, '-0.5');

    // an empty correct answer regex is left out
    nockUpdate('id-ID', {
      type: 'SHORT_ANSWER',
      meta: '',
      config: { statement: 'What is 2 + 2?', score: 2, penalty: -0.5, inputValidationRegex: '.*' },
    });
    await save(user);
  });

  test('updates an essay item', async () => {
    await renderComponent({ item: essayItem });
    const user = userEvent.setup();

    expect(screen.queryByRole('textbox', { name: /^penalty/i })).not.toBeInTheDocument();

    const score = await screen.findByRole('textbox', { name: /^score/i });
    await user.clear(score);
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByText('Must be a number')).toBeInTheDocument();

    await user.type(score, '12');

    nockUpdate('id-ID', { type: 'ESSAY', meta: '', config: { statement: 'Explain.', score: 12 } });
    await save(user);
  });
});
