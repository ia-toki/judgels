import { Icon } from '@blueprintjs/core';
import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { push } from 'react-router-redux';
import createMockStore, { MockStore } from 'redux-mock-store';

import ContentWithSidebarContainer, { ContentWithSidebarContainerProps } from './ContentWithSidebar';
import { AppState } from '../../modules/store';

describe('ContentWithSidebarContainer', () => {
  let store: MockStore<Partial<AppState>>;
  let wrapper: ReactWrapper<any, any>;

  const FirstComponent = () => <div />;
  const SecondComponent = () => <div />;
  const ThirdComponent = () => <div />;

  const render = (childPath: string) => {
    const props: ContentWithSidebarContainerProps = {
      title: 'Content with Sidebar',
      items: [
        {
          id: 'first',
          title: 'First',
          routeComponent: Route,
          component: FirstComponent,
        },
        {
          id: 'second',
          title: 'Second',
          routeComponent: Route,
          component: SecondComponent,
        },
        {
          id: 'third',
          title: 'Third',
          routeComponent: Route,
          component: ThirdComponent,
        },
      ],
    };
    const component = () => <ContentWithSidebarContainer {...props} />;

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/parent' + childPath]}>
          <Route path="/parent" component={component} />
        </MemoryRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    store = createMockStore<Partial<AppState>>()({});
  });

  describe('when the child path is present', () => {
    beforeEach(() => {
      render('/second');
    });

    it('shows sidebar items with the correct texts', () => {
      const items = wrapper.find('[role="tab"]');
      expect(items).toHaveLength(3);

      expect(items.at(0).text()).toEqual('First');
      expect(
        items
          .at(1)
          .childAt(0)
          .text()
      ).toEqual('Second');
      expect(
        items
          .at(2)
          .childAt(0)
          .text()
      ).toEqual('Third');
    });

    it('has the correct active item', () => {
      const items = wrapper.find('[role="tab"]');

      expect(items.at(0).find(Icon)).toHaveLength(0);
      expect(items.at(1).find(Icon)).toHaveLength(1);
      expect(items.at(2).find(Icon)).toHaveLength(0);
    });

    it('renders the active component', () => {
      expect(wrapper.find(SecondComponent)).toHaveLength(1);
    });

    describe('when another item is clicked', () => {
      beforeEach(() => {
        wrapper
          .find('[role="tab"]')
          .at(2)
          .simulate('click');
        wrapper.update();
      });

      it('pushes the url to that item', () => {
        expect(store.getActions()).toContainEqual(push('/parent/third'));
      });
    });
  });

  describe('when the child path is not present', () => {
    beforeEach(() => {
      render('');
    });

    it('has the first item active by default', () => {
      const items = wrapper.find('[role="tab"]');

      expect(items.at(0).find(Icon)).toHaveLength(1);
      expect(items.at(1).find(Icon)).toHaveLength(0);
      expect(items.at(2).find(Icon)).toHaveLength(0);
    });
  });
});
