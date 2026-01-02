import { render, screen, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { TestRouter } from '../../test/RouterWrapper';
import ContentWithTopbar from './ContentWithTopbar';

describe('ContentWithTopbar', () => {
  let store;

  const renderComponent = async (childPath, firstPath) => {
    const items = [
      { path: firstPath || '', title: 'First' },
      { path: 'second', title: 'Second' },
      { path: 'third', title: 'Third' },
    ];

    render(
      <Provider store={store}>
        <TestRouter initialEntries={['/parent' + childPath]}>
          <ContentWithTopbar title="Content with Topbar" basePath="/parent" items={items}>
            <div>Content</div>
          </ContentWithTopbar>
        </TestRouter>
      </Provider>
    );

    await waitFor(() => {
      expect(screen.getAllByRole('link')).toHaveLength(3);
    });
  };

  beforeEach(() => {
    store = createMockStore()({});
  });

  describe('when the first item has a path', () => {
    it('shows topbar items with correct texts', async () => {
      await renderComponent('/second', 'first');

      const items = screen.getAllByRole('link');
      expect(items).toHaveLength(3);
      expect(items[0]).toHaveTextContent('First');
      expect(items[1]).toHaveTextContent('Second');
      expect(items[2]).toHaveTextContent('Third');
    });

    it('marks the matching item as active', async () => {
      await renderComponent('/second', 'first');

      const items = screen.getAllByRole('link');
      expect(items[0]).not.toHaveClass('bp6-active');
      expect(items[1]).toHaveClass('bp6-active');
      expect(items[2]).not.toHaveClass('bp6-active');
    });

    it('has no active item when at base path (first item has non-empty path)', async () => {
      await renderComponent('', 'first');

      const items = screen.getAllByRole('link');
      expect(items[0]).not.toHaveClass('bp6-active');
      expect(items[1]).not.toHaveClass('bp6-active');
    });

    it('generates correct links', async () => {
      await renderComponent('/second', 'first');

      const items = screen.getAllByRole('link');
      expect(items[2]).toHaveAttribute('href', '/parent/third');
    });
  });

  describe('when the first item has empty path', () => {
    it('marks the matching item as active', async () => {
      await renderComponent('/second');

      const items = screen.getAllByRole('link');
      expect(items[0]).not.toHaveClass('bp6-active');
      expect(items[1]).toHaveClass('bp6-active');
    });

    it('marks the first item as active when at base path', async () => {
      await renderComponent('');

      const items = screen.getAllByRole('link');
      expect(items[0]).toHaveClass('bp6-active');
      expect(items[1]).not.toHaveClass('bp6-active');
    });

    it('generates correct link for first item', async () => {
      await renderComponent('/second');

      const items = screen.getAllByRole('link');
      expect(items[0]).toHaveAttribute('href', '/parent');
    });
  });
});
