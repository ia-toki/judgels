import { act, render, screen, waitFor } from '@testing-library/react';

import { TestRouter } from '../../test/RouterWrapper';
import { ContentCard } from '../ContentCard/ContentCard';
import ContentWithSidebar from './ContentWithSidebar';

describe('ContentWithSidebar', () => {
  const renderComponent = async (childPath, firstPath) => {
    const items = [
      { path: firstPath || '', title: 'First' },
      { path: 'second', title: 'Second' },
      { path: 'third', title: 'Third' },
    ];

    await act(async () =>
      render(
        <TestRouter initialEntries={['/parent' + childPath]}>
          <ContentWithSidebar title="Content with Sidebar" basePath="/parent" items={items}>
            <div>Content</div>
          </ContentWithSidebar>
        </TestRouter>
      )
    );

    await waitFor(() => {
      expect(screen.getAllByRole('link')).toHaveLength(3);
    });
  };

  test('when the first item has a path, renders sidebar items with correct texts', async () => {
    await renderComponent('/second', 'first');

    const items = screen.getAllByRole('link');
    expect(items).toHaveLength(3);
    expect(items[0]).toHaveTextContent('First');
    expect(items[1]).toHaveTextContent('Second');
    expect(items[2]).toHaveTextContent('Third');
  });

  test('when the first item has a path, marks the matching item as active', async () => {
    await renderComponent('/second', 'first');

    const items = screen.getAllByRole('link');
    expect(items[0]).not.toHaveAttribute('aria-current');
    expect(items[1]).toHaveAttribute('aria-current', 'page');
    expect(items[2]).not.toHaveAttribute('aria-current');
  });

  test('when the first item has a path, has no active item when at base path', async () => {
    await renderComponent('', 'first');

    const items = screen.getAllByRole('link');
    expect(items[0]).not.toHaveAttribute('aria-current');
    expect(items[1]).not.toHaveAttribute('aria-current');
  });

  test('when the first item has a path, generates correct links', async () => {
    await renderComponent('/second', 'first');

    const items = screen.getAllByRole('link');
    expect(items[2]).toHaveAttribute('href', '/parent/third');
  });

  test('when the first item has empty path, marks the matching item as active', async () => {
    await renderComponent('/second');

    const items = screen.getAllByRole('link');
    expect(items[0]).not.toHaveAttribute('aria-current');
    expect(items[1]).toHaveAttribute('aria-current', 'page');
  });

  test('when the first item has empty path, marks the first item as active when at base path', async () => {
    await renderComponent('');

    const items = screen.getAllByRole('link');
    expect(items[0]).toHaveAttribute('aria-current', 'page');
    expect(items[1]).not.toHaveAttribute('aria-current');
  });

  describe('widgets', () => {
    const Widget = () => <div>Widget</div>;

    const renderAt = async path => {
      const items = [{ path: 'problems', title: 'Problems', widgetComponent: Widget, widgetExact: true }];
      await act(async () =>
        render(
          <TestRouter initialEntries={[path]}>
            <ContentWithSidebar title="Menu" basePath="/parent" items={items}>
              <div>Content</div>
            </ContentWithSidebar>
          </TestRouter>
        )
      );
      await screen.findByText('Content');
    };

    test('shows a widget marked exact on its own path', async () => {
      await renderAt('/parent/problems');
      expect(screen.getByText('Widget')).toBeInTheDocument();
    });

    test('shows a widget marked exact on its own path with a trailing slash', async () => {
      await renderAt('/parent/problems/');
      expect(screen.getByText('Widget')).toBeInTheDocument();
    });

    test('hides a widget marked exact on sub-paths', async () => {
      await renderAt('/parent/problems/some-problem');
      expect(screen.queryByText('Widget')).not.toBeInTheDocument();
    });
  });

  test('gives a heading bar to titled cards at any depth of the page, but not to cards nested in them', async () => {
    await act(async () =>
      render(
        <TestRouter initialEntries={['/parent']}>
          <ContentWithSidebar title="Menu" basePath="/parent" items={[{ path: '', title: 'First' }]}>
            <div>
              <ContentCard title="Page section">
                <ContentCard title="Nested section">Content</ContentCard>
              </ContentCard>
            </div>
          </ContentWithSidebar>
        </TestRouter>
      )
    );

    const pageSection = (await screen.findByText('Page section')).closest('.content-card');
    const nestedSection = screen.getByText('Nested section').closest('.content-card');
    expect(pageSection).toHaveClass('content-card--page-section');
    expect(nestedSection).not.toHaveClass('content-card--page-section');
  });

  test('when the first item has empty path, generates correct link for first item', async () => {
    await renderComponent('/second');

    const items = screen.getAllByRole('link');
    expect(items[0]).toHaveAttribute('href', '/parent');
  });
});
