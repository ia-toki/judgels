import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestFilesPage from './ContestFilesPage';

describe('ContestFilesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    files = [
      { name: 'editorial.pdf', size: 100, lastModifiedTime: 12345 },
      { name: 'solutions.zip', size: 100, lastModifiedTime: 12345 },
    ],
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/files')
      .reply(200, {
        data: files,
        config: { canManage: true },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/files']} path="/contests/$contestSlug/files">
            <ContestFilesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no files', async () => {
    await renderComponent({ files: [] });
    expect(await screen.findByText(/no files/i)).toBeInTheDocument();
    const rows = screen.getAllByRole('row');
    expect(rows).toHaveLength(1 + 0);
  });

  test('renders the files when there are files', async () => {
    await renderComponent();
    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(
      rows.map(row =>
        within(row)
          .queryAllByRole('cell')
          .map(cell => cell.textContent)
      )
    ).toEqual([
      ['', 'Upload new file...'],
      [],
      ['editorial.pdf', '100 B', expect.any(String), ''],
      ['solutions.zip', '100 B', expect.any(String), ''],
    ]);
  });

  test('upload form', async () => {
    await renderComponent({ files: [] });

    const user = userEvent.setup();

    const file = new File(['content'], 'editorial.txt', { type: 'text/plain' });
    Object.defineProperty(file, 'size', { value: 1000 });

    const fileInput = await screen.findByLabelText(/file/i);
    await user.upload(fileInput, file);

    nockUriel()
      .post(
        '/contests/contestJid/files',
        body => body.includes('name="file"') && body.includes('Content-Type: text/plain\r\n\r\ncontent\r\n')
      )
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /upload/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
