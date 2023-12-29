import { mount } from 'enzyme';
import { Component } from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore from 'redux-mock-store';

import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';
import { withBreadcrumb } from './BreadcrumbWrapper';

describe('BreadcrumbWrapper', () => {
  let store;
  let wrapper;
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
    renderFn = jest.fn();

    const WrappedComponent = withBreadcrumb('My Component')(InnerComponent);
    const comp = () => <WrappedComponent num={42} />;

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/component/first']}>
          <Route path="/component" component={comp} />
        </MemoryRouter>
      </Provider>
    );
  });

  it('pushes a new breadcrumb when mounted', () => {
    expect(store.getActions()).toContainEqual(PushBreadcrumb({ link: '/component', title: 'My Component' }));
  });

  it('renders the inner component', () => {
    expect(renderFn).toHaveBeenCalledWith(42);
  });

  it('pops a breadcrumb when unmounted', () => {
    wrapper.unmount();
    expect(store.getActions()).toContainEqual(PopBreadcrumb({ link: '/component' }));
  });
});
