import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { userWebConfigQueryOptions } from '../../modules/queries/userWeb';
import { queryClient } from '../../modules/queryClient';
import { setSession } from '../../modules/session';
import { QueryClientProviderWrapper } from '../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../test/RouterWrapper';
import { nockApi } from '../../utils/nock';
import { UserWidget } from './UserWidget';

describe('UserWidget', () => {
  const setAccountFeatures = ({ userRegistrationEnabled, userResetPasswordEnabled }) => {
    queryClient.setQueryData(userWebConfigQueryOptions().queryKey, {
      userRegistrationEnabled,
      userResetPasswordEnabled,
    });
  };

  beforeEach(() => {
    setAccountFeatures({ userRegistrationEnabled: true, userResetPasswordEnabled: true });
  });

  afterEach(() => {
    queryClient.clear();
  });

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

  test('when not logged in and registration is disabled, hides the register link', async () => {
    setAccountFeatures({ userRegistrationEnabled: false, userResetPasswordEnabled: true });

    const { container } = await renderComponent();
    expect(container.querySelector('[data-key="login"]')).toBeInTheDocument();
    expect(container.querySelector('[data-key="register"]')).not.toBeInTheDocument();
  });

  describe('my account menu item', () => {
    const openMenu = async () => {
      const { container } = await renderComponent({
        user: { jid: 'jid123', username: 'user', email: 'user@domain.com' },
        profile: { username: 'user' },
      });
      await userEvent.setup().click(container.querySelector('.user-widget__profile'));
    };

    test('is shown when only password reset is enabled', async () => {
      setAccountFeatures({ userRegistrationEnabled: false, userResetPasswordEnabled: true });
      await openMenu();
      expect(screen.getByText('My account')).toBeInTheDocument();
    });

    test('is hidden when registration and password reset are disabled', async () => {
      setAccountFeatures({ userRegistrationEnabled: false, userResetPasswordEnabled: false });
      await openMenu();
      expect(screen.getByText('Log out')).toBeInTheDocument();
      expect(screen.queryByText('My account')).not.toBeInTheDocument();
    });
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
    nockApi().post('/v2/session/logout').reply(200);

    const { container } = await renderComponent({
      user: { jid: 'jid123', username: 'user', email: 'user@domain.com' },
      profile: { username: 'user' },
    });

    const user = userEvent.setup();
    await user.click(container.querySelector('.user-widget__profile'));
    await user.click(screen.getByText('Log out'));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
