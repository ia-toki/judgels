import { render } from '@testing-library/react';
import { Component } from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import createMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';
import { withBreadcrumb } from './BreadcrumbWrapper';

describe('BreadcrumbWrapper', () => {
  let store;
  let unmount;
  let renderFn;

  class InnerComponent extends Component {
    render() {
      renderFn(this.props.num);
      return <div />;
    }
  }

  beforeEach(() => {
    store = createMockStore()({
      breadcrumbs: { values: [] },
    });
    renderFn = vi.fn();

    const WrappedComponent = withBreadcrumb('My Component')(InnerComponent);

    const result = render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/component/first']}>
          <Routes>
            <Route path="/component/*" element={<WrappedComponent num={42} />} />
          </Routes>
        </MemoryRouter>
      </Provider>
    );
    unmount = result.unmount;
  });

  it('pushes a new breadcrumb when mounted', () => {
    expect(store.getActions()).toContainEqual(PushBreadcrumb({ link: '/component', title: 'My Component' }));
  });

  it('renders the inner component', () => {
    expect(renderFn).toHaveBeenCalledWith(42);
  });

  it('pops a breadcrumb when unmounted', () => {
    unmount();
    expect(store.getActions()).toContainEqual(PopBreadcrumb({ link: '/component' }));
  });
});
