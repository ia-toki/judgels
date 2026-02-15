import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestFilesPage from './ContestFilesPage';

import * as contestFileActions from '../modules/contestFileActions';

vi.mock('../modules/contestFileActions');

describe('ContestFilesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  let files;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestFileActions.uploadFile.mockReturnValue(Promise.resolve({}));
    contestFileActions.getFiles.mockReturnValue(
      Promise.resolve({
        data: files,
        config: { canManage: true },
      })
    );

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

  describe('when there are no files', () => {
    beforeEach(async () => {
      files = [];
      await renderComponent();
    });

    it('shows placeholder text and no files', async () => {
      expect(await screen.findByText(/no files/i)).toBeInTheDocument();
      const rows = screen.getAllByRole('row');
      expect(rows).toHaveLength(1 + 0);
    });
  });

  describe('when there are files', () => {
    beforeEach(async () => {
      files = [
        {
          name: 'editorial.pdf',
          size: 100,
          lastModifiedTime: 12345,
        },
        {
          name: 'solutions.zip',
          size: 100,
          lastModifiedTime: 12345,
        },
      ];
      await renderComponent();
    });

    it('shows the files', async () => {
      await waitFor(() => {
        expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
      });
      const rows = screen.getAllByRole('row');
      expect(rows).toHaveLength(1 + 1 + 2);
    });
  });

  describe('upload form', () => {
    beforeEach(async () => {
      files = [];
      await renderComponent();
    });

    test('upload form', async () => {
      const user = userEvent.setup();

      const file = new File(['content'], 'editorial.txt', { type: 'text/plain' });
      Object.defineProperty(file, 'size', { value: 1000 });

      const fileInput = await screen.findByLabelText(/file/i);
      await user.upload(fileInput, file);

      const submitButton = screen.getByRole('button', { name: /upload/i });
      await user.click(submitButton);

      expect(contestFileActions.uploadFile).toHaveBeenCalledWith(
        'contestJid',
        expect.objectContaining({
          name: 'editorial.txt',
          size: 1000,
        })
      );
    });
  });
});
