import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore from 'redux-mock-store';

import Menubar from './Menubar';

describe('Menubar', () => {
  let store;

  const FirstComponent = () => <div />;
  const SecondComponent = () => <div />;
  const ThirdComponent = () => <div />;
  const HomeComponent = () => <div />;

  const renderComponent = (childPath, withHome, parentRoute) => {
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

    render(
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
      renderComponent('/second', false, '/parent');
    });

    it('shows menubar items with the correct texts', () => {
      const items = screen.getAllByRole('tab');
      expect(items).toHaveLength(3);

      expect(items[0]).toHaveTextContent('First');
      expect(items[1]).toHaveTextContent('Second');
      expect(items[2]).toHaveTextContent('Third');
    });

    it('has the correct active item', () => {
      const items = screen.getAllByRole('tab');

      expect(items[0]).toHaveAttribute('aria-selected', 'false');
      expect(items[1]).toHaveAttribute('aria-selected', 'true');
      expect(items[2]).toHaveAttribute('aria-selected', 'false');
    });

    it('has the correct links', () => {
      const items = screen.getAllByRole('tab');
      const link = within(items[2]).getByRole('link');
      expect(link).toHaveAttribute('href', '/parent/third');
    });
  });

  describe('when the home path is present', () => {
    beforeEach(() => {
      renderComponent('/', true, '/parent');
    });

    it('shows menubar items with the correct texts', () => {
      const items = screen.getAllByRole('tab');
      expect(items).toHaveLength(4);

      expect(items[0]).toHaveTextContent('Home');
      expect(items[1]).toHaveTextContent('First');
      expect(items[2]).toHaveTextContent('Second');
      expect(items[3]).toHaveTextContent('Third');
    });
  });

  describe('when the parent path is empty', () => {
    beforeEach(() => {
      renderComponent('/second', false, '/');
    });

    it('has the correct active item', () => {
      const items = screen.getAllByRole('tab');

      expect(items[0]).toHaveAttribute('aria-selected', 'false');
      expect(items[1]).toHaveAttribute('aria-selected', 'true');
      expect(items[2]).toHaveAttribute('aria-selected', 'false');
    });

    it('has the correct links', () => {
      const items = screen.getAllByRole('tab');
      const link = within(items[2]).getByRole('link');
      expect(link).toHaveAttribute('href', '/third');
    });
  });
});
