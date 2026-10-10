import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemStatementMediaPage from './ProblemStatementMediaPage';

describe('ProblemStatementMediaPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true, files } = {}) => {
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
      .get('/v4/problems/JIDPROG1/statement/media')
      .reply(200, {
        data: files || [
          { name: 'figure.png', size: 2048, lastModifiedTime: 1700000000000 },
          { name: 'sample.in', size: 3, lastModifiedTime: 1700000000000 },
        ],
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problems/JIDPROG1/media']} path="/admin/problems/$problemJid/media">
            <ProblemStatementMediaPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('heading', { name: /media/i });
  };

  const selectFile = async (user, filename) => {
    const file = new File(['content'], filename);
    await user.upload(document.querySelector('input[type="file"]'), file);
  };

  test('table', async () => {
    await renderComponent();

    const table = screen.getByRole('columnheader', { name: 'Filename' }).closest('table');
    const rows = within(table).getAllByRole('row').slice(1);
    expect(rows.map(row => [...row.querySelectorAll('td')].slice(0, 2).map(cell => cell.textContent))).toEqual([
      ['figure.png', '2.05 kB'],
      ['sample.in', '3 B'],
    ]);
  });

  test('shows a placeholder when there are no files', async () => {
    await renderComponent({ files: [] });

    expect(screen.getByText(/no files/i)).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /delete all/i })).not.toBeInTheDocument();
  });

  test('only lets a viewer download', async () => {
    await renderComponent({ canEdit: false });

    expect(screen.getAllByRole('button').map(button => button.getAttribute('aria-label'))).toEqual([
      'Download figure.png',
      'Download sample.in',
    ]);
  });

  test('uploads a file', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await selectFile(user, 'new.png');

    nockApi().post('/v4/problems/JIDPROG1/statement/media').reply(200);

    await user.click(screen.getByRole('button', { name: /upload/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('uploads a zip', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await selectFile(user, 'media.zip');
    await user.click(screen.getByRole('checkbox', { name: /extract as zip/i }));

    nockApi().post('/v4/problems/JIDPROG1/statement/media/zip').reply(200);

    await user.click(screen.getByRole('button', { name: /upload/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('deletes a file', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: 'Delete figure.png' }));
    expect(await screen.findByText('Delete figure.png?')).toBeInTheDocument();

    nockApi().delete('/v4/problems/JIDPROG1/statement/media/figure.png').reply(200);

    await user.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('deletes all files', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: /delete all/i }));
    expect(await screen.findByText('Delete all media files?')).toBeInTheDocument();

    nockApi().delete('/v4/problems/JIDPROG1/statement/media').reply(200);

    await user.click(screen.getByRole('button', { name: 'Delete' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
