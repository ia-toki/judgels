import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ChapterCreatePage from './ChapterCreatePage';

describe('ChapterCreatePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('creates a chapter', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/chapters/-/new']}>
            <ChapterCreatePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const name = screen.getByRole('textbox');
    await user.type(name, 'New Chapter');

    nockApi().post('/v2/chapters', { name: 'New Chapter' }).reply(200, { jid: 'chapterJid', name: 'New Chapter' });

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
