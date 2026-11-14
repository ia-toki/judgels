import {
  Outlet,
  RouterProvider,
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router';
import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { nockApi } from '../../../../utils/nock';
import LoginPage from '../../login/LoginPage/LoginPage';
import NeedActivationPage from './NeedActivationPage';

describe('NeedActivationPage', () => {
  let router;

  const renderComponent = async initialEntry => {
    const rootRoute = createRootRoute({ component: Outlet });
    router = createRouter({
      routeTree: rootRoute.addChildren([
        createRoute({ getParentRoute: () => rootRoute, path: '/', component: () => <div>Home</div> }),
        createRoute({ getParentRoute: () => rootRoute, path: 'login', component: LoginPage }),
        createRoute({ getParentRoute: () => rootRoute, path: 'need-activation', component: NeedActivationPage }),
      ]),
      history: createMemoryHistory({ initialEntries: [initialEntry] }),
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <RouterProvider router={router} />
        </QueryClientProviderWrapper>
      )
    );
  };

  test('shows the email of a user who logs in before activating', async () => {
    await renderComponent('/login');

    const user = userEvent.setup();
    await user.type(screen.getByRole('textbox', { name: /username or email/i }), 'user');
    await user.type(screen.getByLabelText(/password/i), 'pass');

    nockApi()
      .post('/v2/session/login', { usernameOrEmail: 'user', password: 'pass' })
      .reply(403, { message: 'UserNotActivated', args: { email: 'user@domain.com' } });

    await user.click(screen.getByRole('button', { name: /log in/i }));

    expect(await screen.findByText('user@domain.com')).toBeInTheDocument();
    expect(router.state.location.pathname).toEqual('/need-activation');
  });

  test('redirects home when opened without an email', async () => {
    await renderComponent('/login');

    await act(async () => router.navigate({ to: '/need-activation' }));

    expect(await screen.findByText('Home')).toBeInTheDocument();
    expect(router.state.location.pathname).toEqual('/');
  });
});
