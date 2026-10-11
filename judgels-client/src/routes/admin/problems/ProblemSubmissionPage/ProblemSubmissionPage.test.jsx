import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemSubmissionPage from './ProblemSubmissionPage';

describe('ProblemSubmissionPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ type = 'PROGRAMMING', canEdit = true } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type, authorJid: 'JIDUSER1' });
    nockApi().get('/v4/problems/JIDPROG1/permissions').reply(200, { canEdit });

    if (type === 'PROGRAMMING') {
      nockApi()
        .get('/v4/problems/JIDPROG1/submissions/programming/id/10')
        .reply(200, {
          data: {
            submission: {
              id: 10,
              jid: 'JIDSUBM1',
              userJid: 'JIDUSER1',
              gradingEngine: 'Batch',
              gradingLanguage: 'Cpp17',
              time: 1600000000000,
              latestGrading: { verdict: { code: 'AC' }, score: 100 },
            },
            source: {
              submissionFiles: { source: { name: 'solution.cpp', content: btoa('int main() {}') } },
            },
          },
          profile: { username: 'andi' },
        });
    } else {
      nockApi()
        .get('/v4/problems/JIDPROG1/submissions/bundle/id/10')
        .reply(200, {
          data: {
            id: 10,
            jid: 'JIDSUBM1',
            authorJid: 'JIDUSER1',
            time: 1600000000000,
            latestGrading: {
              score: 3,
              details: { JIDITEM2: { number: 1, score: 4 }, JIDITEM3: { number: 2, score: -1 } },
            },
          },
          answer: { answers: { JIDITEM2: 'a', JIDITEM3: 'tujuh' }, languageCode: 'id-ID' },
          profile: { username: 'andi' },
        });
      nockApi()
        .get('/v4/problems/JIDPROG1/items')
        .reply(200, {
          data: [
            { jid: 'JIDITEM1', type: 'STATEMENT', meta: '', config: {} },
            { jid: 'JIDITEM2', type: 'MULTIPLE_CHOICE', number: 1, meta: '', config: {} },
            { jid: 'JIDITEM3', type: 'SHORT_ANSWER', number: 2, meta: '', config: {} },
            { jid: 'JIDITEM4', type: 'ESSAY', number: 3, meta: '', config: {} },
          ],
        });
    }

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/submissions/10']}
            path="/admin/problems/$problemSlug/submissions/$submissionId"
          >
            <ProblemSubmissionPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText('andi');
  };

  test('programming submission', async () => {
    await renderComponent();

    expect(screen.getByRole('heading', { name: /submission #10/i })).toBeInTheDocument();
    expect(screen.getByText(/C\+\+17/)).toBeInTheDocument();
    // The source is highlighted, which splits it across elements.
    await waitFor(() => expect(document.body.textContent).toContain('int main() {}'));
    expect(screen.getByRole('link', { name: /back/i })).toHaveAttribute(
      'href',
      '/admin/problems/problem-1/submissions'
    );
  });

  test('bundle submission', async () => {
    await renderComponent({ type: 'BUNDLE' });

    const rows = within(screen.getByRole('table')).getAllByRole('row').slice(1);
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['1', 'a', '4'],
      ['2', 'tujuh', '-1'],
      ['3', '-', '-'],
    ]);
    expect(screen.getByText(/score:/i).textContent).toMatch(/3 points/);
  });

  test('regrades a bundle submission', async () => {
    await renderComponent({ type: 'BUNDLE' });
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/submissions/bundle/JIDSUBM1/regrade').reply(200);

    await user.click(screen.getByRole('button', { name: /regrade/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('hides the regrade button when the bundle problem cannot be edited', async () => {
    await renderComponent({ type: 'BUNDLE', canEdit: false });
    expect(screen.queryByRole('button', { name: /regrade/i })).not.toBeInTheDocument();
  });
});
