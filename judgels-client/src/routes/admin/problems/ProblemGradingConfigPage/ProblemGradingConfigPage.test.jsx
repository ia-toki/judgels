import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemGradingConfigPage from './ProblemGradingConfigPage';

describe('ProblemGradingConfigPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const nockProblem = canEdit => {
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
  };

  const renderPage = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/grading/config']}
            path="/admin/problems/$problemSlug/grading/config"
          >
            <ProblemGradingConfigPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const batchConfig = {
    timeLimit: 2000,
    memoryLimit: 65536,
    testData: [
      { id: 0, testCases: [{ input: 'sample_1.in', output: 'sample_1.out', subtaskIds: [0] }] },
      { id: -1, testCases: [{ input: '1.in', output: '1.out', subtaskIds: [-1] }] },
    ],
    customScorer: 'scorer.cpp',
  };

  const batchWithSubtasksConfig = {
    timeLimit: 2000,
    memoryLimit: 65536,
    testData: [
      { id: 0, testCases: [{ input: 'sample_1.in', output: 'sample_1.out', subtaskIds: [0, 1, 2] }] },
      { id: 1, testCases: [{ input: '1.in', output: '1.out', subtaskIds: [1, 2] }] },
    ],
    subtaskPoints: [30, 70],
    customScorer: null,
  };

  const renderComponent = async ({ canEdit = true, engine = 'Batch', config = batchConfig } = {}) => {
    nockProblem(canEdit);
    nockApi().get('/v4/problems/JIDPROG1/grading/config').reply(200, { engine, config });
    nockApi()
      .get('/v4/problems/JIDPROG1/grading/test-data')
      .reply(200, {
        data: ['1.in', '1.out', '2.in', '2.out', 'sample_1.in', 'sample_1.out'].map(name => ({
          name,
          size: 3,
          lastModifiedTime: 1700000000000,
        })),
      });
    nockApi()
      .get('/v4/problems/JIDPROG1/grading/helpers')
      .reply(200, { data: [{ name: 'scorer.cpp', size: 3, lastModifiedTime: 1700000000000 }] });

    await renderPage();
    await screen.findByRole('heading', { name: /config/i });
  };

  const getTestCases = name =>
    within(screen.getByRole('table', { name }))
      .getAllByRole('row')
      .slice(1)
      .map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))
      .filter(cells => cells.some(text => text.endsWith('.in')) && !cells[0].includes('...'));

  test('shows the config of a batch problem', async () => {
    await renderComponent();

    expect(screen.getByRole('textbox', { name: 'Time limit' })).toHaveValue('2000');
    expect(screen.getByRole('textbox', { name: 'Memory limit' })).toHaveValue('65536');
    expect(getTestCases('Sample test cases')).toEqual([['sample_1.in', 'sample_1.out', '']]);
    expect(getTestCases('Test cases')).toEqual([['1.in', '1.out', '']]);
    expect(screen.getByRole('combobox', { name: 'Custom scorer' })).toHaveValue('scorer.cpp');
    expect(screen.queryByRole('table', { name: 'Subtasks' })).not.toBeInTheDocument();
    expect(screen.queryByRole('combobox', { name: 'Communicator' })).not.toBeInTheDocument();
  });

  test('shows the fields of an interactive problem', async () => {
    await renderComponent({
      engine: 'Interactive',
      config: { timeLimit: 2000, memoryLimit: 65536, testData: [], communicator: null },
    });

    expect(screen.getByRole('combobox', { name: 'Communicator' })).toHaveValue('');
    expect(screen.queryByRole('combobox', { name: 'Custom scorer' })).not.toBeInTheDocument();
    expect(screen.queryByRole('combobox', { name: 'Output' })).not.toBeInTheDocument();
  });

  test('shows the fields of an output-only problem', async () => {
    await renderComponent({ engine: 'OutputOnly', config: { timeLimit: 0, memoryLimit: 0, testData: [] } });

    expect(screen.queryByRole('textbox', { name: 'Time limit' })).not.toBeInTheDocument();
    expect(screen.getByRole('combobox', { name: 'Custom scorer' })).toBeInTheDocument();
  });

  test('shows the fields of a functional problem', async () => {
    await renderComponent({
      engine: 'Functional',
      config: { timeLimit: 2000, memoryLimit: 65536, testData: [], sourceFileFieldKeys: ['encoder', 'decoder'] },
    });

    expect(screen.getByRole('textbox', { name: 'Source file keys' })).toHaveValue('encoder,decoder');
  });

  test('is read-only for a viewer', async () => {
    await renderComponent({ canEdit: false });

    expect(screen.getByRole('textbox', { name: 'Time limit' })).toBeDisabled();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });

  test('updates the config of a batch problem', async () => {
    await renderComponent();
    const user = userEvent.setup();

    const timeLimit = screen.getByRole('textbox', { name: 'Time limit' });
    await user.clear(timeLimit);
    await user.type(timeLimit, '3000');

    const testCases = within(screen.getByRole('table', { name: 'Test cases' }));
    await user.selectOptions(testCases.getByRole('combobox', { name: 'Input' }), '2.in');
    expect(testCases.getByRole('combobox', { name: 'Output' })).toHaveValue('2.out');
    await user.click(testCases.getByRole('button', { name: 'Add test case' }));

    await user.click(screen.getByRole('button', { name: 'Remove sample_1.in' }));
    await user.selectOptions(screen.getByRole('combobox', { name: 'Custom scorer' }), '(none)');

    nockApi()
      .put('/v4/problems/JIDPROG1/grading/config', {
        engine: 'Batch',
        config: {
          timeLimit: 3000,
          memoryLimit: 65536,
          testData: [
            { id: 0, testCases: [] },
            {
              id: -1,
              testCases: [
                { input: '1.in', output: '1.out', subtaskIds: [-1] },
                { input: '2.in', output: '2.out', subtaskIds: [-1] },
              ],
            },
          ],
        },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('updates the config of a problem with subtasks', async () => {
    await renderComponent({ engine: 'BatchWithSubtasks', config: batchWithSubtasksConfig });
    const user = userEvent.setup();

    expect(
      within(screen.getByRole('group', { name: 'Subtasks of test group 1' }))
        .getAllByRole('checkbox')
        .filter(checkbox => checkbox.checked)
    ).toHaveLength(2);
    expect(screen.getAllByRole('textbox', { name: /points of subtask/i })).toHaveLength(12);

    await user.click(screen.getByRole('button', { name: 'New test group' }));
    const testGroup = within(screen.getByRole('table', { name: 'Test group 2' }));
    await user.selectOptions(testGroup.getByRole('combobox', { name: 'Input' }), '2.in');
    await user.click(testGroup.getByRole('button', { name: 'Add test case' }));
    await user.click(
      within(screen.getByRole('group', { name: 'Subtasks of test group 2' })).getByRole('checkbox', { name: '3' })
    );
    await user.click(
      within(screen.getByRole('group', { name: 'Subtasks of sample_1.in' })).getByRole('checkbox', { name: '1' })
    );
    await user.type(screen.getByRole('textbox', { name: 'Points of subtask 3' }), '10');

    // an empty test group is dropped
    await user.click(screen.getByRole('button', { name: 'New test group' }));

    nockApi()
      .put('/v4/problems/JIDPROG1/grading/config', {
        engine: 'BatchWithSubtasks',
        config: {
          timeLimit: 2000,
          memoryLimit: 65536,
          testData: [
            { id: 0, testCases: [{ input: 'sample_1.in', output: 'sample_1.out', subtaskIds: [0, 2] }] },
            { id: 1, testCases: [{ input: '1.in', output: '1.out', subtaskIds: [1, 2] }] },
            { id: 2, testCases: [{ input: '2.in', output: '2.out', subtaskIds: [3] }] },
          ],
          subtaskPoints: [30, 70, 10],
        },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('rejects an invalid limit', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.clear(screen.getByRole('textbox', { name: 'Time limit' }));
    await user.click(screen.getByRole('button', { name: 'Save' }));

    expect(await screen.findByText('Time limit must be a positive integer.')).toBeInTheDocument();
  });

  test('auto-populates the test data without saving it', async () => {
    await renderComponent();
    const user = userEvent.setup();

    const timeLimit = screen.getByRole('textbox', { name: 'Time limit' });
    await user.clear(timeLimit);
    await user.type(timeLimit, '3000');

    nockApi()
      .post('/v4/problems/JIDPROG1/grading/config/auto-populate')
      .reply(200, {
        engine: 'Batch',
        config: {
          ...batchConfig,
          testData: [
            { id: 0, testCases: [{ input: 'sample_1.in', output: 'sample_1.out', subtaskIds: [0] }] },
            {
              id: -1,
              testCases: [
                { input: '1.in', output: '1.out', subtaskIds: [-1] },
                { input: '2.in', output: '2.out', subtaskIds: [-1] },
              ],
            },
          ],
        },
      });

    await user.click(screen.getByRole('button', { name: /auto-populate test data from filenames/i }));

    await waitFor(() =>
      expect(getTestCases('Test cases')).toEqual([
        ['1.in', '1.out', ''],
        ['2.in', '2.out', ''],
      ])
    );
    // the edit made before populating is kept
    expect(timeLimit).toHaveValue('3000');
  });
});
