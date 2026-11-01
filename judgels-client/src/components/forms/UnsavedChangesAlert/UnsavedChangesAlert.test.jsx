import {
  Link,
  Outlet,
  RouterProvider,
  createBrowserHistory,
  createRootRoute,
  createRoute,
  createRouter,
  useNavigate,
} from '@tanstack/react-router';
import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Field, Form } from 'react-final-form';

import { FormUnsavedChangesAlert } from './UnsavedChangesAlert';

describe('FormUnsavedChangesAlert', () => {
  let router;
  let user;

  beforeEach(() => {
    window.history.replaceState(null, '', '/');
  });

  afterEach(() => {
    router.history.destroy();
  });

  function EditForm({ onSubmit }) {
    const navigate = useNavigate();

    return (
      <Form onSubmit={() => onSubmit(navigate)}>
        {({ handleSubmit }) => (
          <form onSubmit={e => handleSubmit(e)?.catch(() => {})}>
            <FormUnsavedChangesAlert />
            <Field name="title" component="input" aria-label="Title" />
            <button type="submit">Save</button>
            <Link to="/other">Other page</Link>
            <Link to="/" search={{ page: 2 }}>
              Next page of results
            </Link>
          </form>
        )}
      </Form>
    );
  }

  const renderComponent = async ({ onSubmit = async () => {} } = {}) => {
    const rootRoute = createRootRoute({ component: Outlet });
    const formRoute = createRoute({
      getParentRoute: () => rootRoute,
      path: '/',
      component: () => <EditForm onSubmit={onSubmit} />,
    });
    const otherRoute = createRoute({
      getParentRoute: () => rootRoute,
      path: '/other',
      component: () => <p>Landed on the other page</p>,
    });

    router = createRouter({
      routeTree: rootRoute.addChildren([formRoute, otherRoute]),
      history: createBrowserHistory(),
    });

    await act(async () => render(<RouterProvider router={router} />));

    user = userEvent.setup();
  };

  const saveToServer = () => new Promise(resolve => setTimeout(resolve, 0));

  const clickOtherPage = () => user.click(screen.getByRole('link', { name: 'Other page' }));
  const typeTitle = () => user.type(screen.getByRole('textbox', { name: 'Title' }), 'New title');
  const findAlert = () => screen.findByText('Discard your unsaved changes?');

  test('leaves without asking when nothing is edited', async () => {
    await renderComponent();

    await clickOtherPage();

    await screen.findByText('Landed on the other page');
    expect(screen.queryByText('Discard your unsaved changes?')).not.toBeInTheDocument();
  });

  test('keeps the edits when the user chooses to keep editing', async () => {
    await renderComponent();
    await typeTitle();

    await clickOtherPage();
    await findAlert();
    await user.click(screen.getByRole('button', { name: 'Keep editing' }));

    await waitFor(() => expect(screen.queryByText('Discard your unsaved changes?')).not.toBeInTheDocument());
    expect(router.state.location.pathname).toEqual('/');
    expect(screen.getByRole('textbox', { name: 'Title' })).toHaveValue('New title');
  });

  test('leaves when the user chooses to discard the edits', async () => {
    await renderComponent();
    await typeTitle();

    await clickOtherPage();
    await findAlert();
    await user.click(screen.getByRole('button', { name: 'Discard' }));

    await screen.findByText('Landed on the other page');
  });

  test('stays silent when only the search params change', async () => {
    await renderComponent();
    await typeTitle();

    await user.click(screen.getByRole('link', { name: 'Next page of results' }));

    await waitFor(() => expect(router.state.location.search).toEqual({ page: 2 }));
    expect(screen.queryByText('Discard your unsaved changes?')).not.toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: 'Title' })).toHaveValue('New title');
  });

  test('stays silent when saving navigates away', async () => {
    await renderComponent({
      onSubmit: async navigate => {
        await saveToServer();
        await navigate({ to: '/other' });
      },
    });
    await typeTitle();

    await user.click(screen.getByRole('button', { name: 'Save' }));

    await screen.findByText('Landed on the other page');
    expect(screen.queryByText('Discard your unsaved changes?')).not.toBeInTheDocument();
  });

  test('still asks after a save that failed with a thrown error', async () => {
    await renderComponent({
      onSubmit: async () => {
        await saveToServer();
        throw new Error('Internal server error');
      },
    });
    await typeTitle();
    await user.click(screen.getByRole('button', { name: 'Save' }));

    await clickOtherPage();

    await findAlert();
    expect(router.state.location.pathname).toEqual('/');
  });
});
