import { render, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { UserWidget } from './UserWidget';

describe('UserWidget', () => {
  let user;
  let profile;
  let onRenderAvatar = () => Promise.resolve('url');

  const renderComponent = () => {
    const props = {
      user,
      isWebConfigLoaded: true,
      profile,
      items: [],
      homeRoute: { title: 'Home' },
      onRenderAvatar,
    };

    return render(
      <MemoryRouter>
        <UserWidget {...props} />
      </MemoryRouter>
    );
  };

  beforeEach(() => {
    user = undefined;
    profile = undefined;
  });

  describe('when the user is not logged in', () => {
    it('shows guest links', () => {
      const { container } = renderComponent();
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
      const { container } = renderComponent();
      await waitFor(() => {
        expect(container.querySelector('[data-key="username"]')).toBeInTheDocument();
      });
    });
  });
});
