import { act, render } from '@testing-library/react';

import { TestRouter } from '../../test/RouterWrapper';
import { UserWidget } from './UserWidget';

describe('UserWidget', () => {
  let user;
  let profile;
  let onRenderAvatar = () => Promise.resolve('url');

  const renderComponent = async () => {
    const props = {
      user,
      profile,
      items: [],
      homeRoute: { title: 'Home' },
      onRenderAvatar,
    };

    return await act(async () =>
      render(
        <TestRouter>
          <UserWidget {...props} />
        </TestRouter>
      )
    );
  };

  beforeEach(() => {
    user = undefined;
    profile = undefined;
  });

  describe('when the user is not logged in', () => {
    it('shows guest links', async () => {
      const { container } = await renderComponent();
      expect(container.querySelector('[data-key="login"]')).toBeInTheDocument();
      expect(container.querySelector('[data-key="register"]')).toBeInTheDocument();
    });
  });

  describe('when the user is logged in', () => {
    beforeEach(() => {
      user = { jid: 'jid123', username: 'user', email: 'user@domain.com' };
      profile = { username: 'user' };
    });

    it('shows the user widget', async () => {
      const { container } = await renderComponent();
      expect(container.querySelector('[data-key="username"]')).toBeInTheDocument();
    });
  });
});
