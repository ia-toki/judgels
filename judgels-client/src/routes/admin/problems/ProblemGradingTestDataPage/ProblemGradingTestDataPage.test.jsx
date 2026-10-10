import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemGradingTestDataPage from './ProblemGradingTestDataPage';

describe('ProblemGradingTestDataPage', () => {
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
            initialEntries={['/admin/problems/problem-1/grading/test-data']}
            path="/admin/problems/$problemSlug/grading/test-data"
          >
            <ProblemGradingTestDataPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockProblem(canEdit);
    nockApi()
      .get('/v4/problems/JIDPROG1/grading/test-data')
      .reply(200, {
        data: [
          { name: '1.in', size: 2048, lastModifiedTime: 1700000000000 },
          { name: '1.out', size: 3, lastModifiedTime: 1700000000000 },
        ],
      });

    await renderPage();
    await screen.findByRole('heading', { name: /test data/i });
  };

  const selectFile = async (user, filename) => {
    const file = new File(['content'], filename);
    await user.upload(document.querySelector('input[type="file"]'), file);
  };

  test('table', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('columnheader', { name: 'Filename' }).closest('table'))
      .getAllByRole('row')
      .slice(1);
    expect(rows.map(row => [...row.querySelectorAll('td')].slice(0, 2).map(cell => cell.textContent))).toEqual([
      ['1.in', '2.05 kB'],
      ['1.out', '3 B'],
    ]);
  });

  test('only lets a viewer download', async () => {
    await renderComponent({ canEdit: false });

    expect(screen.getAllByRole('button').map(button => button.getAttribute('aria-label'))).toEqual([
      'Download 1.in',
      'Download 1.out',
    ]);
  });

  test('uploads a file', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await selectFile(user, 'new.txt');

    nockApi().post('/v4/problems/JIDPROG1/grading/test-data').reply(200);

    await user.click(screen.getByRole('button', { name: /upload/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('uploads a zip', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await selectFile(user, 'files.zip');
    await user.click(screen.getByRole('checkbox', { name: /extract as zip/i }));

    nockApi().post('/v4/problems/JIDPROG1/grading/test-data/zip').reply(200);

    await user.click(screen.getByRole('button', { name: /upload/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('deletes a file', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: 'Delete 1.in' }));
    expect(await screen.findByText('Delete 1.in?')).toBeInTheDocument();

    nockApi().delete('/v4/problems/JIDPROG1/grading/test-data/1.in').reply(200);

    await user.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('deletes all files', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /delete all/i }));
    expect(await screen.findByText('Delete all test data files?')).toBeInTheDocument();

    nockApi().delete('/v4/problems/JIDPROG1/grading/test-data').reply(200);

    await user.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
