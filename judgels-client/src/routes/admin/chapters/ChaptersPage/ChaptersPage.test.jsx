import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import ChaptersPage from './ChaptersPage';

describe('ChaptersPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    chapters = [
      { jid: 'JIDCHAPTER1', id: 1, name: 'Chapter 1' },
      { jid: 'JIDCHAPTER2', id: 2, name: 'Chapter 2' },
    ],
  } = {}) => {
    nockJerahmeel().get('/chapters').reply(200, { data: chapters });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/chapters']}>
            <ChaptersPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no chapters', async () => {
    await renderComponent({ chapters: [] });
    expect(await screen.findByText(/no chapters/i)).toBeInTheDocument();
  });

  test('renders the chapters table', async () => {
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
    ).toEqual([[], ['1', 'JIDCHAPTER1', 'Chapter 1'], ['2', 'JIDCHAPTER2', 'Chapter 2']]);
  });

  test('renders the create button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new chapter/i })).toBeInTheDocument();
  });
});
