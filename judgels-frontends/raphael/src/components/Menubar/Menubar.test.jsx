import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore from 'redux-mock-store';

import Menubar from './Menubar';

describe('Menubar', () => {
  let store;
  let wrapper;

  const FirstComponent = () => <div />;
  const SecondComponent = () => <div />;
  const ThirdComponent = () => <div />;
  const HomeComponent = () => <div />;

  const render = (childPath, withHome, parentRoute) => {
    const props = {
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

    const initialPath = parentRoute === '/' ? childPath : parentRoute + childPath;

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={[initialPath]}>
          <Route path={parentRoute} component={component} />
        </MemoryRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    store = createMockStore()({});
  });

  describe('when the child path is present', () => {
    beforeEach(() => {
      render('/second', false, '/parent');
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

    it('has the correct links', () => {
      const link = wrapper
        .find('[role="tab"]')
        .at(2)
        .find('a');
      expect(link.props().href).toEqual('/parent/third');
    });
  });

  describe('when the home path is present', () => {
    beforeEach(() => {
      render('/', true, '/parent');
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

  describe('when the parent path is empty', () => {
    beforeEach(() => {
      render('/second', false, '/');
    });

    it('has the correct active item', () => {
      const items = wrapper.find('[role="tab"]');

      expect(items.at(0).prop('aria-selected')).toEqual(false);
      expect(items.at(1).prop('aria-selected')).toEqual(true);
      expect(items.at(2).prop('aria-selected')).toEqual(false);
    });

    it('has the correct links', () => {
      const link = wrapper
        .find('[role="tab"]')
        .at(2)
        .find('a');
      expect(link.props().href).toEqual('/third');
    });
  });
});
