import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { push } from 'react-router-redux';
import createMockStore, { MockStore } from 'redux-mock-store';

import Menubar, { MenubarProps } from './Menubar';
import { AppState } from '../../modules/store';

describe('Menubar', () => {
  let store: MockStore<Partial<AppState>>;
  let wrapper: ReactWrapper<any, any>;

  const FirstComponent = () => <div />;
  const SecondComponent = () => <div />;
  const ThirdComponent = () => <div />;
  const HomeComponent = () => <div />;

  const render = (childPath: string, withHome: boolean) => {
    const props: MenubarProps = {
      items: [
        {
          id: 'first',
          title: 'First',
          route: {
            path: '/first',
            component: FirstComponent,
          },
        },
        {
          id: 'second',
          title: 'Second',
          route: {
            path: '/second',
            component: SecondComponent,
          },
        },
        {
          id: 'third',
          title: 'Third',
          route: {
            path: '/third',
            component: ThirdComponent,
          },
        },
      ],
      homeRoute: withHome
        ? {
            id: 'home',
            title: 'Home',
            route: {
              path: '/',
              component: HomeComponent,
            },
          }
        : undefined,
    };
    const component = () => <Menubar {...props} />;

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
      render('/second', false);
    });

    it('shows menubar items with the correct texts', () => {
      const items = wrapper.find('[role="tab"]');
      expect(items).toHaveLength(3);

      expect(items.at(0).text()).toEqual('First');
      expect(items.at(1).text()).toEqual('Second');
      expect(items.at(2).text()).toEqual('Third');
    });

    it('has the correct active item', () => {
      const items = wrapper.find('[role="tab"]');

      expect(items.at(0).prop('aria-selected')).toEqual(false);
      expect(items.at(1).prop('aria-selected')).toEqual(true);
      expect(items.at(2).prop('aria-selected')).toEqual(false);
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

  describe('when the home path is present', () => {
    beforeEach(() => {
      render('/', true);
    });

    it('shows menubar items with the correct texts', () => {
      const items = wrapper.find('[role="tab"]');
      expect(items).toHaveLength(4);

      expect(items.at(0).text()).toEqual('Home');
      expect(items.at(1).text()).toEqual('First');
      expect(items.at(2).text()).toEqual('Second');
      expect(items.at(3).text()).toEqual('Third');
    });
  });
});
