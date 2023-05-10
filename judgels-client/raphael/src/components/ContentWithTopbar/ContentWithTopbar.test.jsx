import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore from 'redux-mock-store';

import ContentWithTopbar from './ContentWithTopbar';

describe('ContentWithTopbar', () => {
  let store;
  let wrapper;

  const FirstComponent = () => <div />;
  const SecondComponent = () => <div />;
  const ThirdComponent = () => <div />;

  const render = (childPath, firstId) => {
    const props = {
      title: 'Content with Topbar',
      items: [
        {
          id: firstId || '@',
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
    const component = () => <ContentWithTopbar {...props} />;

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/parent' + childPath]}>
          <Route path="/parent" component={component} />
        </MemoryRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    store = createMockStore()({});
  });

  describe('when the first route is allowed to have suffix', () => {
    describe('when the child path is present', () => {
      beforeEach(() => {
        render('/second', 'first');
      });

      it('shows sidebar items with the correct texts', () => {
        const items = wrapper.find('a');
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
        const items = wrapper.find('a');

        expect(items.at(0).hasClass('bp4-active')).toBeFalsy();
        expect(items.at(1).hasClass('bp4-active')).toBeTruthy();
        expect(items.at(2).hasClass('bp4-active')).toBeFalsy();
      });

      it('renders the active component', () => {
        expect(wrapper.find(SecondComponent)).toBeTruthy();
      });

      it('has the correct links', () => {
        const link = wrapper.find('a').at(2);
        expect(link.props().href).toEqual('/parent/third');
      });
    });

    describe('when the child path is not present', () => {
      beforeEach(() => {
        render('', 'first');
      });

      it('has the first item active by default', () => {
        const items = wrapper.find('a');

        expect(items.at(0).hasClass('bp4-active')).toBeTruthy();
        expect(items.at(1).hasClass('bp4-active')).toBeFalsy();
        expect(items.at(2).hasClass('bp4-active')).toBeFalsy();
      });
    });
  });

  describe('when the first route is not allowed to have suffix', () => {
    describe('when the child path is present', () => {
      beforeEach(() => {
        render('/second');
      });

      it('shows sidebar items with the correct texts', () => {
        const items = wrapper.find('a');
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
        const items = wrapper.find('a');

        expect(items.at(0).hasClass('bp4-active')).toBeFalsy();
        expect(items.at(1).hasClass('bp4-active')).toBeTruthy();
        expect(items.at(2).hasClass('bp4-active')).toBeFalsy();
      });

      it('renders the active component', () => {
        expect(wrapper.find(SecondComponent)).toBeTruthy();
      });

      it('has the correct first link without suffix', () => {
        const link = wrapper
          .find('a')
          .at(0)
          .find('a');
        expect(link.props().href).toEqual('/parent/');
      });
    });

    describe('when the child path is not present', () => {
      beforeEach(() => {
        render('');
      });

      it('has the first item active by default', () => {
        const items = wrapper.find('a');

        expect(items.at(0).hasClass('bp4-active')).toBeTruthy();
        expect(items.at(1).hasClass('bp4-active')).toBeFalsy();
        expect(items.at(2).hasClass('bp4-active')).toBeFalsy();
      });
    });
  });
});
