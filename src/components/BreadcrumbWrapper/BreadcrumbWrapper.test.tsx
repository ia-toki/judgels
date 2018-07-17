import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore, { MockStore } from 'redux-mock-store';

import { AppState } from '../../modules/store';
import { withBreadcrumb } from './BreadcrumbWrapper';
import { AddBreadcrumb, DelBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

describe('BreadcrumbWrapper', () => {
  let store: MockStore<Partial<AppState>>;
  let wrapper: ReactWrapper<any, any>;
  let renderFn: jest.Mock<any>;

  interface InnerComponentProps {
    num: number;
  }

  class InnerComponent extends React.Component<InnerComponentProps> {
    render() {
      renderFn(this.props.num);
      return <div />;
    }
  }

  beforeEach(() => {
    store = createMockStore<Partial<AppState>>()({
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
    expect(store.getActions()).toContainEqual(AddBreadcrumb.create({ link: '/component', title: 'My Component' }));
  });

  it('renders the inner component', () => {
    expect(renderFn).toHaveBeenCalledWith(42);
  });

  it('pops a breadcrumb when unmounted', () => {
    wrapper.unmount();
    expect(store.getActions()).toContainEqual(DelBreadcrumb.create({ link: '/component' }));
  });
});
