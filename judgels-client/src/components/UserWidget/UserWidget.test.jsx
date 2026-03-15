import { act, render } from '@testing-library/react';

import { QueryClientProviderWrapper } from '../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../test/RouterWrapper';
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
});
