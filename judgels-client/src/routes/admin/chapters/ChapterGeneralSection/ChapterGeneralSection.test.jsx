import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ChapterGeneralSection } from './ChapterGeneralSection';

describe('ChapterGeneralSection', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const chapter = {
    id: 1,
    jid: 'JIDCHAPTER1',
    name: 'Chapter 1',
  };

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ChapterGeneralSection chapter={chapter} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders general details', async () => {
    await renderComponent();

    const table = screen.getByRole('table');
    expect(
      screen
        .getAllByRole('row')
        .map(row => screen.getAllByRole('cell', { container: row }).map(cell => cell.textContent))
    );
  });

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Chapter 1');
    await user.clear(name);
    await user.type(name, 'New Chapter');

    nockJerahmeel().post('/chapters/JIDCHAPTER1', { name: 'New Chapter' }).reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
