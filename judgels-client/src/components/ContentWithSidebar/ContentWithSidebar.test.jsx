import { render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore from 'redux-mock-store';

import ContentWithSidebar from './ContentWithSidebar';

describe('ContentWithSidebar', () => {
  let store;
  const FirstComponent = () => <div>One</div>;
  const SecondComponent = () => <div>Two</div>;
  const ThirdComponent = () => <div>Three</div>;

  const renderComponent = (childPath, firstId) => {
    const props = {
      title: 'Content with Sidebar',
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
    const component = () => <ContentWithSidebar {...props} />;

    render(
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
        renderComponent('/second', 'first');
      });

      it('shows sidebar items with the correct texts', () => {
        const items = screen.getAllByRole('tab');
        expect(items).toHaveLength(3);

        expect(items[0]).toHaveTextContent('First');
        expect(items[1]).toHaveTextContent('Second');
        expect(items[2]).toHaveTextContent('Third');
      });

      it('has the correct active item', () => {
        const items = screen.getAllByRole('tab');

        expect(within(items[0]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
        expect(within(items[1]).getByRole('img', { hidden: true })).toBeInTheDocument();
        expect(within(items[2]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      });

      it('renders the active component', () => {
        expect(screen.getByText('Two')).toBeInTheDocument();
      });

      it('has the correct links', () => {
        const items = screen.getAllByRole('tab');
        const link = within(items[2]).getByRole('link');
        expect(link).toHaveAttribute('href', '/parent/third');
      });
    });

    describe('when the child path is not present', () => {
      beforeEach(() => {
        renderComponent('', 'first');
      });

      it('has the first item active by default', () => {
        const items = screen.getAllByRole('tab');

        expect(within(items[0]).getByRole('img', { hidden: true })).toBeInTheDocument();
        expect(within(items[1]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
        expect(within(items[2]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      });
    });
  });

  describe('when the first route is not allowed to have suffix', () => {
    describe('when the child path is present', () => {
      beforeEach(() => {
        renderComponent('/second');
      });

      it('shows sidebar items with the correct texts', () => {
        const items = screen.getAllByRole('tab');
        expect(items).toHaveLength(3);

        expect(items[0]).toHaveTextContent('First');
        expect(items[1]).toHaveTextContent('Second');
        expect(items[2]).toHaveTextContent('Third');
      });

      it('has the correct active item', () => {
        const items = screen.getAllByRole('tab');

        expect(within(items[0]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
        expect(within(items[1]).getByRole('img', { hidden: true })).toBeInTheDocument();
        expect(within(items[2]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      });

      it('renders the active component', () => {
        expect(screen.getByRole('tab', { selected: true })).toBeInTheDocument();
      });

      it('has the correct first link without suffix', () => {
        const items = screen.getAllByRole('tab');
        const link = within(items[0]).getByRole('link');
        expect(link).toHaveAttribute('href', '/parent/');
      });
    });

    describe('when the child path is not present', () => {
      beforeEach(() => {
        renderComponent('');
      });

      it('has the first item active by default', () => {
        const items = screen.getAllByRole('tab');

        expect(within(items[0]).getByRole('img', { hidden: true })).toBeInTheDocument();
        expect(within(items[1]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
        expect(within(items[2]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      });
    });
  });
});
