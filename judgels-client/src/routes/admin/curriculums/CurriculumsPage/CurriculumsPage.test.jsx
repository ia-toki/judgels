import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import CurriculumsPage from './CurriculumsPage';

describe('CurriculumsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    curriculums = [{ jid: 'JIDCURRICULUM1', name: 'Curriculum 1', description: 'Description 1' }],
  } = {}) => {
    nockApi().get('/curriculums').reply(200, { data: curriculums });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/curriculums']}>
            <CurriculumsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no curriculums', async () => {
    await renderComponent({ curriculums: [] });
    expect(await screen.findByText(/no curriculums/i)).toBeInTheDocument();
  });

  test('renders the curriculums table', async () => {
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
    ).toEqual([[], ['Curriculum 1']]);
  });
});
