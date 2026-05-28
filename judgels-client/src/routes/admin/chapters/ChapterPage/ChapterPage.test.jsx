import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import ChapterPage from './ChapterPage';

describe('ChapterPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJerahmeel()
      .get('/chapters')
      .reply(200, {
        data: [
          {
            id: 1,
            jid: 'JIDCHAPTER1',
            name: 'Chapter 1',
          },
        ],
      });

    nockJerahmeel()
      .get('/chapters/JIDCHAPTER1/lessons')
      .reply(200, {
        data: [{ alias: 'A', lessonJid: 'JIDLESSON1' }],
        lessonsMap: { JIDLESSON1: { slug: 'lesson-1' } },
      });

    nockJerahmeel()
      .get('/chapters/JIDCHAPTER1/problems')
      .reply(200, {
        data: [{ alias: 'A', problemJid: 'JIDPROBLEM1', type: 'PROGRAMMING' }],
        problemsMap: { JIDPROBLEM1: { slug: 'problem-1' } },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/chapters/JIDCHAPTER1']} path="/admin/chapters/$chapterJid">
            <ChapterPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders chapter details', async () => {
    await renderComponent();

    await screen.findAllByText(/Chapter 1/);

    const tables = screen.getAllByRole('table');

    expect(
      within(tables[0])
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([['Name', 'Chapter 1']]);

    expect(
      within(tables[1])
        .getAllByRole('row')
        .slice(1)
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([['A', 'lesson-1']]);

    expect(
      within(tables[2])
        .getAllByRole('row')
        .slice(1)
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([['A', 'problem-1', 'PROGRAMMING']]);
  });
});
