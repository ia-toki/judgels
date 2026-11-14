import {
  Outlet,
  RouterProvider,
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router';
import { act, render, screen } from '@testing-library/react';

import { VerdictCode } from '../../../../../../../../../modules/api/gradingVerdict';
import { QueryClientProviderWrapper } from '../../../../../../../../../test/QueryClientProviderWrapper';
import { nockApi } from '../../../../../../../../../utils/nock';
import ChapterProblemStatementPage from './ChapterProblemStatementPage';

describe('ChapterProblemStatementPage', () => {
  let router;

  const renderComponent = async progress => {
    const worksheet = {
      problem: { problemJid: 'problemJid1', alias: 'B', type: 'BUNDLE' },
      progress,
      worksheet: {
        statement: { title: 'Problem', text: 'This is problem description' },
        items: [],
      },
    };

    nockApi().get('/v2/courses/slug/courseSlug').reply(200, { jid: 'courseJid', slug: 'courseSlug' });
    nockApi().get('/v2/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });
    nockApi().get('/v4/training/submissions/bundle/answers').query(true).reply(200, {});

    const rootRoute = createRootRoute({ component: Outlet });
    const problemRoute = createRoute({
      getParentRoute: () => rootRoute,
      path: '/courses/$courseSlug/chapters/$chapterAlias/problems/$problemAlias',
      component: () => <ChapterProblemStatementPage worksheet={worksheet} />,
    });
    const statementRoute = createRoute({ getParentRoute: () => problemRoute, path: '/' });
    const submissionsRoute = createRoute({ getParentRoute: () => problemRoute, path: 'submissions' });
    router = createRouter({
      routeTree: rootRoute.addChildren([problemRoute.addChildren([statementRoute, submissionsRoute])]),
      history: createMemoryHistory({ initialEntries: ['/courses/courseSlug/chapters/chapter-1/problems/B'] }),
      defaultPendingMinMs: 0,
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <RouterProvider router={router} />
        </QueryClientProviderWrapper>
      )
    );
  };

  test('stays on the statement while the problem is not answered', async () => {
    await renderComponent({ verdict: VerdictCode.PND });

    expect(await screen.findByText('This is problem description')).toBeInTheDocument();
    expect(router.state.location.pathname).toEqual('/courses/courseSlug/chapters/chapter-1/problems/B');
  });

  test('moves to the submissions once the problem is answered', async () => {
    await renderComponent({ verdict: VerdictCode.AC });

    expect(await screen.findByText('Submission received.')).toBeInTheDocument();
    expect(router.state.location.pathname).toEqual('/courses/courseSlug/chapters/chapter-1/problems/B/submissions');
  });

  test('stays on the statement after Retake', async () => {
    await renderComponent({ verdict: VerdictCode.AC });
    expect(await screen.findByText('Submission received.')).toBeInTheDocument();

    await act(async () => router.navigate({ to: '/courses/courseSlug/chapters/chapter-1/problems/B' }));

    expect(router.state.location.pathname).toEqual('/courses/courseSlug/chapters/chapter-1/problems/B');
    expect(screen.queryByText('Submission received.')).not.toBeInTheDocument();
  });
});
