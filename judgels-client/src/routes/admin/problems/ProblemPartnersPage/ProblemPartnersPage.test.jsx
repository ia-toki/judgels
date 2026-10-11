import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemPartnersPage from './ProblemPartnersPage';

describe('ProblemPartnersPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ partners } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1/partners')
      .reply(200, {
        data: partners || [
          { userJid: 'JIDUSER1', permission: 'UPDATE' },
          { userJid: 'JIDUSER2', permission: 'VIEW' },
        ],
        profilesMap: {
          JIDUSER1: { username: 'andi' },
          JIDUSER2: { username: 'budi' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/partners']}
            path="/admin/problems/$problemSlug/partners"
          >
            <ProblemPartnersPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('heading', { name: /partners/i });
  };

  const openEditForm = async user => {
    await user.click(screen.getByRole('button', { name: /edit/i }));
    return await screen.findByRole('textbox');
  };

  test('partners', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('table')).getAllByRole('row').slice(1);
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['andi', 'View and update'],
      ['budi', 'View'],
    ]);
  });

  test('no partners', async () => {
    await renderComponent({ partners: [] });

    expect(screen.getByText('No partners.')).toBeInTheDocument();
    expect(screen.queryByRole('table')).not.toBeInTheDocument();
  });

  test('edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const csv = await openEditForm(user);
    expect(csv).toHaveValue('andi,UPDATE\nbudi,VIEW');

    await user.clear(csv);
    await user.type(csv, 'budi\n\ncaca, VIEW\n');

    nockApi()
      .put('/v4/problems/JIDPROG1/partners', [
        { username: 'budi', permission: 'UPDATE' },
        { username: 'caca', permission: 'VIEW' },
      ])
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test.each([
    ['andi,EDIT', 'Invalid permission: EDIT'],
    ['andi,VIEW,UPDATE', 'Invalid row: andi,VIEW,UPDATE'],
    ['andi\nandi,VIEW', 'Duplicate user: andi'],
  ])('rejects the invalid CSV %j', async (value, error) => {
    await renderComponent();

    const user = userEvent.setup();
    const csv = await openEditForm(user);
    await user.clear(csv);
    await user.type(csv, value);

    await user.click(screen.getByRole('button', { name: /save/i }));

    expect(await screen.findByText(error)).toBeInTheDocument();
  });

  test('shows which partners are not found', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const csv = await openEditForm(user);
    await user.type(csv, '\nghost');

    nockApi()
      .put('/v4/problems/JIDPROG1/partners')
      .reply(400, { message: 'ProblemPartnerUsernamesNotFound', args: { usernames: 'ghost' } });

    await user.click(screen.getByRole('button', { name: /save/i }));

    expect(await screen.findByText('Users not found: ghost')).toBeInTheDocument();
  });
});
