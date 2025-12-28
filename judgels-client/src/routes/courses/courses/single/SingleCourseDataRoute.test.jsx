import { act, render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import courseReducer from '../modules/courseReducer';
import SingleCourseDataRoute from './SingleCourseDataRoute';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseActions from '../modules/courseActions';

vi.mock('../modules/courseActions');
vi.mock('../../../../modules/breadcrumbs/breadcrumbsActions');

describe('SingleCourseDataRoute', () => {
  const renderComponent = currentPath => {
    const store = createStore(
      combineReducers({
        jerahmeel: combineReducers({ course: courseReducer }),
      }),
      applyMiddleware(thunk)
    );

    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={[currentPath]}>
          <Routes>
            <Route path="/courses/:courseSlug" element={<SingleCourseDataRoute />} />
          </Routes>
        </MemoryRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    courseActions.getCourseBySlug.mockReturnValue(() => Promise.resolve({ jid: 'jid123', name: 'Course 123' }));
    courseActions.clearCourse.mockReturnValue({ type: 'clear' });

    breadcrumbsActions.pushBreadcrumb.mockReturnValue({ type: 'push' });
    breadcrumbsActions.popBreadcrumb.mockReturnValue({ type: 'pop' });
  });

  test('navigation', async () => {
    await act(async () => {
      renderComponent('/courses/basic');
    });

    expect(courseActions.getCourseBySlug).toHaveBeenCalledWith('basic');
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith('/courses/basic', 'Course 123');
  });
});
