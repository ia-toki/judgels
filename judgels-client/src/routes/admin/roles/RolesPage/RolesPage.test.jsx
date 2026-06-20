import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import RolesPage from './RolesPage';

describe('RolesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    roles = [
      { userJid: 'userJid1', role: { account: 'ADMIN', problem: 'ADMIN', contest: 'ADMIN', training: 'ADMIN' } },
      { userJid: 'userJid2', role: { problem: 'ADMIN' } },
    ],
    profilesMap = {
      userJid1: { username: 'andi' },
      userJid2: { username: 'budi' },
    },
  } = {}) => {
    nockApi().get('/user-roles').reply(200, {
      data: roles,
      profilesMap,
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/roles']}>
            <RolesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no roles', async () => {
    await renderComponent({ roles: [], profilesMap: {} });
    expect(await screen.findByText(/no roles/i)).toBeInTheDocument();
  });

  test('renders the roles table', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(
      rows.map(row =>
        within(row)
          .queryAllByRole('cell')
          .map(cell => cell.textContent)
      )
    ).toEqual([[], ['andi', 'Admin', 'Admin', 'Admin', 'Admin'], ['budi', '-', 'Admin', '-', '-']]);
  });

  test('renders the edit button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /edit roles/i })).toBeInTheDocument();
  });

  const roleSelect = label => screen.getByRole('button', { name: label });

  const selectRole = async (user, label, optionName) => {
    await user.click(roleSelect(label));
    await user.click(await screen.findByRole('menuitem', { name: optionName }));
  };

  test('form', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit roles/i }));

    expect(roleSelect('account role for andi')).toHaveTextContent('Admin');
    expect(roleSelect('problem role for andi')).toHaveTextContent('Admin');
    expect(roleSelect('contest role for andi')).toHaveTextContent('Admin');
    expect(roleSelect('training role for andi')).toHaveTextContent('Admin');
    expect(roleSelect('account role for budi')).toHaveTextContent('-');
    expect(roleSelect('problem role for budi')).toHaveTextContent('Admin');

    await user.click(screen.getByRole('img', { name: 'remove budi' }));

    // Re-adding andi replaces all their roles; caca is added. Both get only the problem role.
    await user.click(screen.getByRole('button', { name: /add users/i }));
    await user.type(screen.getByLabelText(/usernames/i), 'andi\ncaca');
    await selectRole(user, 'new problem role', 'Admin');
    await user.click(screen.getByRole('button', { name: /^add$/i }));

    expect(roleSelect('account role for andi')).toHaveTextContent('-');
    expect(roleSelect('contest role for andi')).toHaveTextContent('-');
    expect(roleSelect('training role for andi')).toHaveTextContent('-');
    expect(roleSelect('problem role for andi')).toHaveTextContent('Admin');

    await selectRole(user, 'contest role for caca', 'Admin');

    nockApi()
      .put('/user-roles', {
        andi: { problem: 'ADMIN' },
        caca: { problem: 'ADMIN', contest: 'ADMIN' },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
