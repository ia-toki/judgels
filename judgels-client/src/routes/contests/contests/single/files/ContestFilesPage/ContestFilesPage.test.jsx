import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestFilesPage from './ContestFilesPage';

import * as contestFileActions from '../modules/contestFileActions';

vi.mock('../modules/contestFileActions');

describe('ContestFilesPage', () => {
  let files;

  const renderComponent = async () => {
    contestFileActions.uploadFile.mockReturnValue(() => Promise.resolve({}));
    contestFileActions.getFiles.mockReturnValue(() =>
      Promise.resolve({
        data: files,
        config: { canManage: true },
      })
    );

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <MemoryRouter>
            <ContestFilesPage />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('when there are no files', () => {
    beforeEach(async () => {
      files = [];
      await renderComponent();
    });

    it('shows placeholder text and no files', () => {
      expect(screen.getByText(/no files/i)).toBeInTheDocument();
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

    it('shows the files', () => {
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

      const fileInput = screen.getByLabelText(/file/i);
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
