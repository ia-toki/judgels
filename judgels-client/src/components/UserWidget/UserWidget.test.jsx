import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../modules/session';
import { QueryClientProviderWrapper } from '../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../test/RouterWrapper';
import { nockApi } from '../../utils/nock';
import { UserWidget } from './UserWidget';

describe('UserWidget', () => {
  const renderComponent = async ({ user, profile } = {}) => {
    const props = {
      user,
      profile,
      items: [],
      homeRoute: { title: 'Home' },
    };

    return await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <UserWidget {...props} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('when not logged in, renders guest links', async () => {
    const { container } = await renderComponent();
    expect(container.querySelector('[data-key="login"]')).toBeInTheDocument();
    expect(container.querySelector('[data-key="register"]')).toBeInTheDocument();
  });

  test('when logged in, renders the user widget', async () => {
    const { container } = await renderComponent({
      user: { jid: 'jid123', username: 'user', email: 'user@domain.com' },
      profile: { username: 'user' },
    });
    expect(container.querySelector('[data-key="username"]')).toBeInTheDocument();
  });

  test('logs out in place', async () => {
    setSession('token', { jid: 'jid123' });
    nockApi().post('/session/logout').reply(200);

    const { container } = await renderComponent({
      user: { jid: 'jid123', username: 'user', email: 'user@domain.com' },
      profile: { username: 'user' },
    });

    const user = userEvent.setup();
    await user.click(container.querySelector('.widget-user__profile'));
    await user.click(screen.getByText('Log out'));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
