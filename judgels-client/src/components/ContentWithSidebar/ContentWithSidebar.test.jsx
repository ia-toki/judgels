import { act, render, screen, waitFor, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { TestRouter } from '../../test/RouterWrapper';
import ContentWithSidebar from './ContentWithSidebar';

describe('ContentWithSidebar', () => {
  let store;

  const renderComponent = async (childPath, firstPath) => {
    const items = [
      { path: firstPath || '', title: 'First' },
      { path: 'second', title: 'Second' },
      { path: 'third', title: 'Third' },
    ];

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter initialEntries={['/parent' + childPath]}>
            <ContentWithSidebar title="Content with Sidebar" basePath="/parent" items={items}>
              <div>Content</div>
            </ContentWithSidebar>
          </TestRouter>
        </Provider>
      )
    );

    await waitFor(() => {
      expect(screen.getAllByRole('tab')).toHaveLength(3);
    });
  };

  beforeEach(() => {
    store = createMockStore()({});
  });

  describe('when the first item has a path', () => {
    it('shows sidebar items with correct texts', async () => {
      await renderComponent('/second', 'first');

      const items = screen.getAllByRole('tab');
      expect(items).toHaveLength(3);
      expect(items[0]).toHaveTextContent('First');
      expect(items[1]).toHaveTextContent('Second');
      expect(items[2]).toHaveTextContent('Third');
    });

    it('marks the matching item as active', async () => {
      await renderComponent('/second', 'first');

      const items = screen.getAllByRole('tab');
      expect(within(items[0]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      expect(within(items[1]).getByRole('img', { hidden: true })).toBeInTheDocument();
      expect(within(items[2]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
    });

    it('has no active item when at base path (first item has non-empty path)', async () => {
      await renderComponent('', 'first');

      const items = screen.getAllByRole('tab');
      expect(within(items[0]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      expect(within(items[1]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
    });

    it('generates correct links', async () => {
      await renderComponent('/second', 'first');

      const items = screen.getAllByRole('tab');
      expect(within(items[2]).getByRole('link')).toHaveAttribute('href', '/parent/third');
    });
  });

  describe('when the first item has empty path', () => {
    it('marks the matching item as active', async () => {
      await renderComponent('/second');

      const items = screen.getAllByRole('tab');
      expect(within(items[0]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
      expect(within(items[1]).getByRole('img', { hidden: true })).toBeInTheDocument();
    });

    it('marks the first item as active when at base path', async () => {
      await renderComponent('');

      const items = screen.getAllByRole('tab');
      expect(within(items[0]).getByRole('img', { hidden: true })).toBeInTheDocument();
      expect(within(items[1]).queryByRole('img', { hidden: true })).not.toBeInTheDocument();
    });

    it('generates correct link for first item', async () => {
      await renderComponent('/second');

      const items = screen.getAllByRole('tab');
      expect(within(items[0]).getByRole('link')).toHaveAttribute('href', '/parent');
    });
  });
});
