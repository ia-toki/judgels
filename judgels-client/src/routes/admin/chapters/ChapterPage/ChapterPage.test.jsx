import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ChapterPage from './ChapterPage';

describe('ChapterPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi()
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

    nockApi()
      .get('/chapters/JIDCHAPTER1/lessons')
      .reply(200, {
        data: [{ alias: 'A', lessonJid: 'JIDLESSON1' }],
        lessonsMap: { JIDLESSON1: { slug: 'lesson-1' } },
      });

    nockApi()
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

  test('details', async () => {
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

  test('general form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[0]);

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Chapter 1');
    await user.clear(name);
    await user.type(name, 'New Chapter');

    nockApi().post('/chapters/JIDCHAPTER1', { name: 'New Chapter' }).reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('lessons form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[1]);

    const lessons = document.querySelector('textarea[name="lessons"]');
    expect(lessons).toHaveValue('A,lesson-1');
    await user.clear(lessons);
    await user.type(lessons, 'A,intro');

    nockApi()
      .put('/chapters/JIDCHAPTER1/lessons', [{ alias: 'A', slug: 'intro' }])
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('problems form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[2]);

    const problems = document.querySelector('textarea[name="problems"]');
    expect(problems).toHaveValue('A,problem-1');
    await user.clear(problems);
    await user.type(problems, 'A,new-problem');

    nockApi()
      .put('/chapters/JIDCHAPTER1/problems', [{ alias: 'A', slug: 'new-problem', type: 'PROGRAMMING' }])
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
