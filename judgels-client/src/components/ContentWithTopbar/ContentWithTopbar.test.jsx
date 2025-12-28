import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Navigate, Route, Routes } from 'react-router-dom';
import createMockStore from 'redux-mock-store';

import ContentWithTopbar from './ContentWithTopbar';

describe('ContentWithTopbar', () => {
  let store;
  const FirstComponent = () => <div>One</div>;
  const SecondComponent = () => <div>Two</div>;
  const ThirdComponent = () => <div>Three</div>;

  const renderComponent = (childPath, firstPath) => {
    const items = [
      {
        path: firstPath !== undefined ? firstPath : '',
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
    ];

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
            <Route
              path="/parent/*"
              element={
                <ContentWithTopbar title="Content with Topbar" items={items}>
                  {children}
                </ContentWithTopbar>
              }
            />
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
        const items = screen.getAllByRole('link');
        expect(items).toHaveLength(3);

        expect(items[0]).toHaveTextContent('First');
        expect(items[1]).toHaveTextContent('Second');
        expect(items[2]).toHaveTextContent('Third');
      });

      it('has the correct active item', () => {
        const items = screen.getAllByRole('link');

        expect(items[0]).not.toHaveClass('bp6-active');
        expect(items[1]).toHaveClass('bp6-active');
        expect(items[2]).not.toHaveClass('bp6-active');
      });

      it('renders the active component', () => {
        expect(screen.getByText('Two')).toBeInTheDocument();
      });

      it('has the correct links', () => {
        const items = screen.getAllByRole('link');
        expect(items[2]).toHaveAttribute('href', '/parent/third');
      });
    });

    describe('when the child path is not present', () => {
      beforeEach(() => {
        renderComponent('', 'first');
      });

      it('has the first item active by default', () => {
        const items = screen.getAllByRole('link');

        expect(items[0]).toHaveClass('bp6-active');
        expect(items[1]).not.toHaveClass('bp6-active');
        expect(items[2]).not.toHaveClass('bp6-active');
      });
    });
  });

  describe('when the first route is not allowed to have suffix', () => {
    describe('when the child path is present', () => {
      beforeEach(() => {
        renderComponent('/second');
      });

      it('shows sidebar items with the correct texts', () => {
        const items = screen.getAllByRole('link');
        expect(items).toHaveLength(3);

        expect(items[0]).toHaveTextContent('First');
        expect(items[1]).toHaveTextContent('Second');
        expect(items[2]).toHaveTextContent('Third');
      });

      it('has the correct active item', () => {
        const items = screen.getAllByRole('link');

        expect(items[0]).not.toHaveClass('bp6-active');
        expect(items[1]).toHaveClass('bp6-active');
        expect(items[2]).not.toHaveClass('bp6-active');
      });

      it('renders the active component', () => {
        expect(screen.getByText('Two')).toBeInTheDocument();
      });

      it('has the correct first link without suffix', () => {
        const items = screen.getAllByRole('link');
        expect(items[0]).toHaveAttribute('href', '/parent/');
      });
    });

    describe('when the child path is not present', () => {
      beforeEach(() => {
        renderComponent('');
      });

      it('has the first item active by default', () => {
        const items = screen.getAllByRole('link');

        expect(items[0]).toHaveClass('bp6-active');
        expect(items[1]).not.toHaveClass('bp6-active');
        expect(items[2]).not.toHaveClass('bp6-active');
      });
    });
  });
});
