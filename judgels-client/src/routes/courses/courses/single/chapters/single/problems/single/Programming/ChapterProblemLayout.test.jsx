import {
  Outlet,
  RouterProvider,
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router';
import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsReducer';
import { QueryClientProviderWrapper } from '../../../../../../../../../test/QueryClientProviderWrapper';
import { nockJerahmeel } from '../../../../../../../../../utils/nock';
import chapterProblemReducer from '../modules/chapterProblemReducer';
import ChapterProblemLayout from './ChapterProblemLayout';

describe('ChapterProblemLayout', () => {
  beforeEach(async () => {
    const worksheet = {
      problem: {
        problemJid: 'problemJid1',
        alias: 'A',
        type: 'PROGRAMMING',
      },
      defaultLanguage: 'id',
      languages: ['en', 'id'],
      skeletons: [],
      worksheet: {
        statement: {
          title: 'Problem',
          text: 'This is problem description',
        },
        limits: {},
        submissionConfig: {
          sourceKeys: ['source'],
          gradingEngine: 'Batch',
          gradingLanguageRestriction: {
            allowedLanguageNames: [],
            isAllowedAll: true,
          },
        },
      },
    };
    const renderNavigation = () => null;

    nockJerahmeel().get('/courses/slug/courseSlug').reply(200, { jid: 'courseJid', slug: 'courseSlug' });
    nockJerahmeel().get('/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        jerahmeel: combineReducers({
          chapterProblem: chapterProblemReducer,
        }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutStatementLanguage('en'));

    const rootRoute = createRootRoute({ component: Outlet });
    const layoutRoute = createRoute({
      getParentRoute: () => rootRoute,
      path: '/courses/$courseSlug/chapters/$chapterAlias',
      component: () => <ChapterProblemLayout worksheet={worksheet} renderNavigation={renderNavigation} />,
    });
    const childRoute = createRoute({
      getParentRoute: () => layoutRoute,
      path: '/',
      component: () => <div>Child content</div>,
    });
    const routeTree = rootRoute.addChildren([layoutRoute.addChildren([childRoute])]);
    const router = createRouter({
      routeTree,
      history: createMemoryHistory({ initialEntries: ['/courses/courseSlug/chapters/chapter-1'] }),
      defaultPendingMinMs: 0,
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <RouterProvider router={router} />
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('renders problem statement', async () => {
    expect(await screen.findByText('This is problem description')).toBeInTheDocument();
  });

  test('renders topbar navigation', async () => {
    expect(await screen.findByText('Code')).toBeInTheDocument();
    expect(screen.getByText('Submissions')).toBeInTheDocument();
  });

  test('renders outlet content', async () => {
    expect(await screen.findByText('Child content')).toBeInTheDocument();
  });
});
