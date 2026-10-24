import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ContestCreatePage from './ContestCreatePage';

describe('ContestCreatePage', () => {
  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/contests/-/new']}>
            <ContestCreatePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    await user.type(slug, 'new-contest');

    nockApi().post('/v2/contests', { slug: 'new-contest' }).reply(200);

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
