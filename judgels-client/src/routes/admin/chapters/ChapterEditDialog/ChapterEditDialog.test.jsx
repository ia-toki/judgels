import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ChapterEditDialog } from './ChapterEditDialog';

const chapter = {
  id: 1,
  jid: 'chapterJid',
  name: 'Chapter',
};

describe('ChapterEditDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit dialog form', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ChapterEditDialog isOpen={true} chapter={chapter} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const name = screen.getByRole('textbox');
    expect(name).toHaveValue('Chapter');
    await user.clear(name);
    await user.type(name, 'New chapter');

    nockJerahmeel().post('/chapters/chapterJid', { name: 'New chapter' }).reply(200);

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
