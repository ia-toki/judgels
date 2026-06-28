import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../utils/nock';
import ChangeAvatarPage from './ChangeAvatarPage';

describe('ChangeAvatarPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'JIDUSER1' });
  });

  const renderComponent = async avatarExists => {
    nockApi().get('/users/JIDUSER1/avatar/exists').reply(200, avatarExists);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ChangeAvatarPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders upload form with current avatar', async () => {
    await renderComponent(true);

    expect(await screen.findByText('Current avatar')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /remove avatar/i })).toBeInTheDocument();
    expect(screen.getByText('Upload new avatar')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /upload/i })).toBeInTheDocument();
  });

  test('renders upload form without current avatar', async () => {
    await renderComponent(false);

    expect(await screen.findByText('Upload new avatar')).toBeInTheDocument();
    expect(screen.queryByText('Current avatar')).not.toBeInTheDocument();
  });
});
