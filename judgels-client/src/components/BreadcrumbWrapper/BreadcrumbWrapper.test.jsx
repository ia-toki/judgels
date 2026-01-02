import { render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';
import { TestRouter } from '../../test/RouterWrapper';
import { withBreadcrumb } from './BreadcrumbWrapper';

describe('BreadcrumbWrapper', () => {
  let store;

  beforeEach(() => {
    store = createMockStore()({
      breadcrumbs: { values: [] },
    });
  });

  it('pushes breadcrumb on mount and pops on unmount', async () => {
    const renderFn = vi.fn();
    const InnerComponent = ({ num }) => {
      renderFn(num);
      return <div>Inner</div>;
    };

    const WrappedComponent = withBreadcrumb('My Component')(InnerComponent);

    const { unmount } = render(
      <Provider store={store}>
        <TestRouter initialEntries={['/component']}>
          <WrappedComponent num={42} />
        </TestRouter>
      </Provider>
    );

    await waitFor(() => {
      expect(store.getActions()).toContainEqual(PushBreadcrumb({ link: '/component', title: 'My Component' }));
    });

    expect(renderFn).toHaveBeenCalledWith(42);

    unmount();
    expect(store.getActions()).toContainEqual(PopBreadcrumb({ link: '/component' }));
  });
});
