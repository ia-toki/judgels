import { act, render } from '@testing-library/react';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../test/RouterWrapper';
import { UserWidget } from './UserWidget';

describe('UserWidget', () => {
  let user;
  let profile;

  afterEach(() => {
    nock.cleanAll();
  });

  const renderComponent = async () => {
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
