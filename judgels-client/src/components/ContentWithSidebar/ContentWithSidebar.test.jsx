import { render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Navigate, Route, Routes } from 'react-router-dom';
import createMockStore from 'redux-mock-store';

import ContentWithSidebar from './ContentWithSidebar';

describe('ContentWithSidebar', () => {
  let store;
  const FirstComponent = () => <div>One</div>;
  const SecondComponent = () => <div>Two</div>;
  const ThirdComponent = () => <div>Three</div>;

  const renderComponent = (childPath, firstPath) => {
    const props = {
      title: 'Content with Sidebar',
      basePath: '/parent',
      items: [
        {
          path: firstPath || '',
          title: 'First',
        },
        {
          path: 'second',
          title: 'Second',
        },
        {
          path: 'third',
          title: 'Third',
        },
      ],
    };

    const children =
      firstPath && firstPath !== '' ? (
        <Routes>
          <Route index element={<Navigate to={firstPath} replace />} />
          <Route path={firstPath} element={<FirstComponent />} />
          <Route path="second" element={<SecondComponent />} />
          <Route path="third" element={<ThirdComponent />} />
        </Routes>
      ) : (
        <Routes>
          <Route index element={<FirstComponent />} />
          <Route path="second" element={<SecondComponent />} />
          <Route path="third" element={<ThirdComponent />} />
        </Routes>
      );

    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/parent' + childPath]}>
          <Routes>
            <Route path="/parent/*" element={<ContentWithSidebar {...props}>{children}</ContentWithSidebar>} />
          </Routes>
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
