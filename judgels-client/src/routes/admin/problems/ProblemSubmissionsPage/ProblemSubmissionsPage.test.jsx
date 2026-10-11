import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemSubmissionsPage from './ProblemSubmissionsPage';

describe('ProblemSubmissionsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
    window.confirm = () => true;
  });

  const renderComponent = async ({ type = 'PROGRAMMING', canEdit = true } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type, authorJid: 'JIDUSER1' });
    nockApi().get('/v4/problems/JIDPROG1/permissions').reply(200, { canEdit });

    if (type === 'PROGRAMMING') {
      nockApi()
        .get('/v4/problems/JIDPROG1/submissions/programming')
        .reply(200, {
          data: {
            page: [
              {
                id: 20,
                jid: 'JIDSUBM2',
                userJid: 'JIDUSER2',
                gradingLanguage: 'Cpp17',
                time: 1700000000000,
                latestGrading: { verdict: { code: 'WA' }, score: 40 },
              },
              {
                id: 10,
                jid: 'JIDSUBM1',
                userJid: 'JIDUSER1',
                gradingLanguage: 'Python3',
                time: 1600000000000,
                latestGrading: { verdict: { code: 'AC' }, score: 100 },
              },
            ],
            totalCount: 2,
          },
          profilesMap: { JIDUSER1: { username: 'andi' }, JIDUSER2: { username: 'budi' } },
        });
    } else {
      nockApi()
        .get('/v4/problems/JIDPROG1/submissions/bundle')
        .reply(200, {
          data: {
            page: [
              {
                id: 20,
                jid: 'JIDSUBM2',
                authorJid: 'JIDUSER2',
                time: 1700000000000,
                latestGrading: { score: 7, details: {} },
              },
              {
                id: 10,
                jid: 'JIDSUBM1',
                authorJid: 'JIDUSER1',
                time: 1600000000000,
                latestGrading: { score: -1, details: {} },
              },
            ],
            totalCount: 2,
          },
          profilesMap: { JIDUSER1: { username: 'andi' }, JIDUSER2: { username: 'budi' } },
        });
    }

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/submissions']}
            path="/admin/problems/$problemSlug/submissions"
          >
            <ProblemSubmissionsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('table');
  };

  const getRows = columns =>
    within(screen.getByRole('table'))
      .getAllByRole('row')
      .slice(1)
      .map(row => columns.map(column => row.querySelectorAll('td')[column].textContent.trim()));

  test('programming submissions', async () => {
    await renderComponent();

    expect(getRows([1, 2, 3])).toEqual([
      ['budi', 'C++17', 'Wrong Answer40'],
      ['andi', 'Python 3', 'Accepted'],
    ]);
    expect(
      screen
        .getAllByRole('link')
        .map(link => link.getAttribute('href'))
        .filter(href => href.includes('/submissions/'))
    ).toEqual(['/admin/problems/problem-1/submissions/20', '/admin/problems/problem-1/submissions/10']);
  });

  test('regrades a programming submission', async () => {
    await renderComponent();
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/submissions/programming/JIDSUBM1/regrade').reply(200);

    await user.click(screen.getByTitle('Regrade submission 10'));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('regrades all programming submissions', async () => {
    await renderComponent();
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/submissions/programming/regrade').reply(200);

    await user.click(screen.getByRole('button', { name: /regrade all/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('bundle submissions', async () => {
    await renderComponent({ type: 'BUNDLE' });

    expect(getRows([1, 2])).toEqual([
      ['budi', '7'],
      ['andi', '-1'],
    ]);
  });

  test('regrades a bundle submission', async () => {
    await renderComponent({ type: 'BUNDLE' });
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/submissions/bundle/JIDSUBM1/regrade').reply(200);

    await user.click(screen.getByTitle('Regrade submission 10'));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('regrades all bundle submissions', async () => {
    await renderComponent({ type: 'BUNDLE' });
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/submissions/bundle/regrade').reply(200);

    await user.click(screen.getByRole('button', { name: /regrade all/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test.each(['PROGRAMMING', 'BUNDLE'])('hides the regrade actions when the %s problem cannot be edited', async type => {
    await renderComponent({ type, canEdit: false });

    expect(screen.queryByRole('button', { name: /regrade all/i })).not.toBeInTheDocument();
    expect(screen.queryByTitle('Regrade submission 10')).not.toBeInTheDocument();
  });
});
