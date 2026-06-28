import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemsPage from './ProblemsPage';

describe('ProblemsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/problems')
      .query(true)
      .reply(200, {
        data: { page: [], totalCount: 0 },
        problemsMap: {},
        problemMetadatasMap: {},
        problemDifficultiesMap: {},
        problemProgressesMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ProblemsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders filter placeholder when no tags selected', async () => {
    await renderComponent();

    expect(await screen.findByText(/select some filters on the left/i)).toBeInTheDocument();
  });
});
