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
import courseReducer, { PutCourse } from '../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../modules/courseChapterReducer';
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

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        jerahmeel: combineReducers({
          course: courseReducer,
          courseChapter: courseChapterReducer,
          chapterProblem: chapterProblemReducer,
        }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutCourse({ jid: 'courseJid', slug: 'courseSlug' }));
    store.dispatch(
      PutCourseChapter({
        jid: 'chapterJid',
        name: 'Chapter 1',
        alias: 'chapter-1',
        courseSlug: 'courseSlug',
      })
    );
    store.dispatch(PutStatementLanguage('en'));

    const rootRoute = createRootRoute({ component: Outlet });
    const layoutRoute = createRoute({
      getParentRoute: () => rootRoute,
      path: '/test',
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
      history: createMemoryHistory({ initialEntries: ['/test'] }),
      defaultPendingMinMs: 0,
    });

    await act(async () =>
      render(
        <Provider store={store}>
          <RouterProvider router={router} />
        </Provider>
      )
    );
  });

  test('renders problem statement', () => {
    expect(screen.getByText('This is problem description')).toBeInTheDocument();
  });

  test('renders topbar navigation', () => {
    expect(screen.getByText('Code')).toBeInTheDocument();
    expect(screen.getByText('Submissions')).toBeInTheDocument();
  });

  test('renders outlet content', () => {
    expect(screen.getByText('Child content')).toBeInTheDocument();
  });
});
