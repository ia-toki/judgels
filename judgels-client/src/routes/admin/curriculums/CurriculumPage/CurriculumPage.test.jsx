import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import CurriculumPage from './CurriculumPage';

describe('CurriculumPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/curriculums')
      .reply(200, { data: [{ jid: 'JIDCURRICULUM1', name: 'Curriculum 1', description: 'Description 1' }] });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/curriculums/JIDCURRICULUM1']} path="/admin/curriculums/$curriculumJid">
            <CurriculumPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await screen.findAllByText(/Curriculum 1/);

    const table = screen.getByRole('table');

    expect(
      within(table)
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([
      ['Name', 'Curriculum 1'],
      ['Description', 'Description 1'],
    ]);
  });

  test('general form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

    const name = screen.getByRole('textbox', { name: /^name/i });
    expect(name).toHaveValue('Curriculum 1');
    await user.clear(name);
    await user.type(name, 'New Curriculum');

    const description = document.querySelector('textarea[name="description"]');
    expect(description).toHaveValue('Description 1');
    await user.clear(description);
    await user.type(description, 'New Description');

    nockApi()
      .post('/curriculums/JIDCURRICULUM1', {
        name: 'New Curriculum',
        description: 'New Description',
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
